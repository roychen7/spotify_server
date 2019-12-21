package com.spotify__server.components.accessers.spotify_api_access;

// package dependencies
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.components.data.Data;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;

import java.io.IOException;


public class InitModulesApi {


    @Autowired
    private DatabaseAccesser database_accesser;

    @Autowired
    private PlaylistDatabaseAccesser playlist_database_accesser;
    
    @Autowired
    private Data data;

    private HttpClient client = HttpClients.createDefault();

    // grabs playlist ids and names associated with given user ID, simultaneously updates them into 
    // the database
    public List<Pair<String, String>> getAndUpdatePlaylistIdsAndNames(String user_id)
            throws ClientProtocolException, IOException, ParseException {
        System.out.println("SpotifyApiAccesser::getAndUpdatePlaylistIdsAndNames");
        HttpGet get = new HttpGet("https://api.spotify.com/v1/users/" + user_id + "/playlists");
        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        System.out.println(database_accesser.getAccessToken());
        List<Pair<String, String>> ret_list = new ArrayList<>();

        HttpResponse http_response = client.execute(get);
        String response_string = EntityUtils.toString(http_response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject json_response_object = (JSONObject) parser.parse(response_string);

        System.out.println(json_response_object.get("items").getClass());
        JSONArray resp_array = (JSONArray) json_response_object.get("items");

        for (int i = 0; i < resp_array.size(); i++) {
            JSONObject obj = (JSONObject) resp_array.get(i);
            ret_list.add(new Pair(obj.getAsString("id"), obj.getAsString("name")));

            database_accesser.updateIntoDb("insert ignore into `playlists` set `playlist_id`='"
                    + ret_list.get(i).getKey() + "', `playlist_name`='" + ret_list.get(i).getValue() + "'");
        }

        playlist_database_accesser.updatePlaylistIds();
        return ret_list;
    }


    // grabs songs associated with the playlist with @param1 as ID, and updates them into user's database
    // @param2 is for calculating playlist index offsets as Spotify's API only allows a maximum 100
    // song retrievals per request, and offsets allow us to grab the remaining songs
    public void updateSongsPlaylistsArtists(String playlist_id, int noOfResets)
            throws ClientProtocolException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlist_id
                + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists%2Curi))");

        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        System.out.println(database_accesser.getAccessToken());

        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);
        JSONArray items_array = (JSONArray) obj.get("items");

        for (int i = 0; i < items_array.size(); i++) {
            // grabbing relevant track information
            JSONObject ith_track = (JSONObject) items_array.get(i);
            JSONObject track_details = (JSONObject) ith_track.get("track");
            String song_name = track_details.getAsString("name");
            String song_id = track_details.getAsString("id");
            String song_duration = track_details.getAsString("duration_ms");
            String song_uri = track_details.getAsString("uri");
            JSONArray artist_json_array = (JSONArray) track_details.get("artists");

            // initializing song's artists in database
            List<String> artist_id_list = new ArrayList<>();
            for (int j = 0; j < artist_json_array.size(); j++) {
                JSONObject temp_obj = (JSONObject) artist_json_array.get(j);
                String artist_id = temp_obj.getAsString("id");
                artist_id_list.add(artist_id);
            }

            String insert_string = "";
            String reformatted_name = "";

            // index of any quotation marks (eg. ') in song name. If there exists, must
            // handle (eg. handleQuotation())
            int ind_of_quote = -1;

                if ((ind_of_quote = song_name.indexOf("'")) == -1) {
                    insert_string = "insert ignore into `songs` values ('" + song_id + "', '" + song_uri + "', '" + song_name + "', '" + 
                    song_duration + "', '0', NULL)";
                } else {
                    reformatted_name = handleQuotation(song_name, ind_of_quote);
                    insert_string = "insert ignore into `songs` values ('" + song_id + "', '" + song_uri + "', '" + reformatted_name + "', '" + 
                    song_duration + "', '0', NULL)";
                }

            if (!insert_string.equals("")) {
                System.out.println("INSERT STRING IS: " + insert_string);
                database_accesser.updateIntoDb(insert_string);
                updateSongsToPlaylistsTable(song_id, ind_of_quote == -1 ? song_name : reformatted_name, playlist_id);
                updateSongsToArtistsTable(song_id, ind_of_quote == -1 ? song_name : reformatted_name, artist_id_list);
            }

            // if we have reached the 100th song, need to re-fetch the next however many
            // (max of 100) items in the playlist b/c spotify only
            // allows a maximum of 100 songs per fetch
            if (i == 99) {
                System.out.println("resetting i to 0");
                System.out.println("noOfResets: " + noOfResets);
                i = 0;
                noOfResets += 100;
                String fetchString = "https://api.spotify.com/v1/playlists/" + playlist_id
                        + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists%2Curi))&offset="
                        + Integer.toString(noOfResets);
                System.out.println(fetchString);
                get = new HttpGet(fetchString);
                get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
                System.out.println(database_accesser.getAccessToken());
                response = client.execute(get);
                response_string = EntityUtils.toString(response.getEntity());
                obj = (JSONObject) parser.parse(response_string);
                items_array = (JSONArray) obj.get("items");
                System.out.println("items_array size: " + items_array.size());
            }
        }

        eagerInitializeArtistsAssociatedPlaylists(data.getArtists());
    }

    // helper function, updates and stores the auto-playlist recommendation results of each
    //  artist in the cache by calling updateAssociatedPlaylists method
    private void eagerInitializeArtistsAssociatedPlaylists(Set<String> all_artist_ids) {
        for (String s : all_artist_ids) {
            playlist_database_accesser.updateAssociatedPlaylists(s);
        }
    }

    // helper function, appends a second "'" character everytime we encounter one 
    private String handleQuotation(String song_name, int ind_of_quote) {  
        StringBuilder ret = new StringBuilder(song_name.substring(0, ind_of_quote));
        
        for (int i = ind_of_quote; i < song_name.length(); i++) {
            if (song_name.charAt(i) == "'".charAt(0)) {
                ret.append("''");
                continue;
            }
            ret.append(song_name.charAt(i));
        }
        return ret.toString();
    }

    // updates songsToPlaylists table
    private void updateSongsToPlaylistsTable(String song_id, String song_name, String playlist_id) {
        database_accesser.updateIntoDb("insert into `songsToPlaylists` values ('" + song_id + "', '" + song_name + "', '" + playlist_id + "')"); 
    }

    // updates songsToArtists table
    private void updateSongsToArtistsTable(String song_id, String song_name, List<String> artist_id_list) {
        for (int i = 0; i < artist_id_list.size(); i++) {
            database_accesser.updateIntoDb("insert ignore into `songsToArtists` values ('" + song_id + "', '" + song_name + "', '" + artist_id_list.get(i) + "')"); 
            data.addToArtists(artist_id_list.get(i));
        }
    }
}