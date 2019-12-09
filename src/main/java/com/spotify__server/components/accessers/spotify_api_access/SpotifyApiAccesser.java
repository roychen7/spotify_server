package com.spotify__server.components.accessers.spotify_api_access;

// package dependencies
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.HelperClass;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

@Component
public class SpotifyApiAccesser {

    @Autowired
    private DatabaseAccesser database_accesser;

    @Autowired
    private PlaylistDatabaseAccesser playlist_database_accesser;

    private HttpClient client = HttpClients.createDefault();

    public String getUserId() throws ClientProtocolException, IOException, ParseException {

        HttpGet get = new HttpGet("https://api.spotify.com/v1/me");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");

        // grab access token and add it to get header
        String access_token = database_accesser.getAccessToken();
        get.addHeader("Authorization", "Bearer " + access_token);

        // execute the get call and save the user id as user_id
        HttpResponse resp = client.execute(get);
        HttpEntity entity = (HttpEntity) resp.getEntity();
        String entity_string = EntityUtils.toString(entity);
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(entity_string);
        jsonObj = (JSONObject) parser.parse(entity_string);
        return jsonObj.getAsString("id");
    }


    public List<Pair<String, String>> getAndUpdatePlaylistIdsAndNames(String user_id)
            throws ClientProtocolException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/users/" + user_id + "/playlists");
        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
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

            database_accesser.insertIntoDb("insert ignore into `playlists` set `playlist_id`='"
                    + ret_list.get(i).getKey() + "', `playlist_name`='" + ret_list.get(i).getValue() + "'");
        }

        playlist_database_accesser.updatePlaylistIds();
        return ret_list;
    }

    public Song getSongDetails(String song_id) throws org.apache.http.ParseException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/tracks/" + song_id);
        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());

        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);

        String song_name = obj.getAsString("name");
        String song_uri = obj.getAsString("uri");
        String song_duration = obj.getAsString("duration_ms");

        String playlist_id = database_accesser.getSingleFromDb("select `playlist_id` from `songs` where `song_uri`='" + song_uri);
        return new Song(playlist_id, song_uri, song_name, song_duration);
    }


    public void updatePlaylistSongsIntoDbFromApi(String playlist_id, int noOfResets)
            throws ClientProtocolException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlist_id
                + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists%2Curi))");

        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());

        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);
        JSONArray items_array = (JSONArray) obj.get("items");

        for (int i = 0; i < items_array.size(); i++) {
            JSONObject ith_track = (JSONObject) items_array.get(i);
            JSONObject track_details = (JSONObject) ith_track.get("track");
            String song_name = track_details.getAsString("name");
            String song_id = track_details.getAsString("id");
            String song_duration = track_details.getAsString("duration_ms");
            String song_uri = track_details.getAsString("uri");

            String insert_string = "";

            // index of any quotation marks (eg. ') in song name. If there exists, must
            // handle (eg. handleQuotation())
            int ind_of_quote = -1;

                if ((ind_of_quote = song_name.indexOf("'")) == -1) {
                    // insert_string = "insert ignore into `songs` set `playlist_id`='" + playlist_id + "', `song_id`='"
                    //         + song_id + "', `song_uri`='" + song_uri + "', `song_name`='" + song_name + "', `song_duration`='" 
                    //         + song_duration + "', `date_played`='" + null + "'";
                    insert_string = "insert ignore into `songs` values ('" + playlist_id + "', '" + song_uri + "', '" + song_name + "', '" + 
                    song_duration + "', '0', 'NULL'";
                } else {
                    String song_name_reformatted = handleQuotation(song_name, ind_of_quote);
                    // insert_string = "insert ignore into `songs` set `playlist_id`='" + playlist_id + "', `song_id`='"
                    //         + song_id + "', `song_uri`='" + song_uri + "', `song_name`='" + song_name_reformatted + "', `song_duration`='"
                    //         + song_duration + "', `date_played`='" + null + "'";
                    insert_string = "insert ignore into `songs` values ('" + playlist_id + "', '" + song_uri + "', '" + song_name_reformatted + "', '" + 
                    song_duration + "', '0', 'NULL'";
                }

            if (!insert_string.equals("")) {
                database_accesser.insertIntoDb(insert_string);
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

                response = client.execute(get);
                response_string = EntityUtils.toString(response.getEntity());
                obj = (JSONObject) parser.parse(response_string);
                items_array = (JSONArray) obj.get("items");
                System.out.println("items_array size: " + items_array.size());
            }
        }
    }


    public void refreshToken() throws FileNotFoundException, SQLException, IOException, ParseException {
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");

        String refresh_token = database_accesser.getSingleFromDb("select `refresh_token` from `token`");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", refresh_token));

        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        post.addHeader("Authorization",
                "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse resp = client.execute(post);

        HttpEntity entity = resp.getEntity();
        String jsonString = HelperClass.getResponseString(entity);

        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(jsonString);

        String access_token = (String) jsonObj.get("access_token");
        database_accesser.insertIntoDb("update `token` set `access_token`='" + access_token + "'");
        database_accesser.updateAccessToken();
        System.out.println("Token::/refresh: updated token!");
    }


    public void togglePlay() throws ClientProtocolException, IOException {
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");

        String access_token = database_accesser.getAccessToken();
        put.addHeader("Authorization", "Bearer " + access_token);

        client.execute(put);
    }


    public void togglePause() throws ClientProtocolException, IOException {
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/pause");

        String access_token = database_accesser.getAccessToken();
        put.addHeader("Authorization", "Bearer " + access_token);

        client.execute(put);
    }


    public void playSongs(Queue<Song> songs) throws ClientProtocolException, IOException {
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
        put.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        put.addHeader("Content-type", "application/json");

        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        
        while (!songs.isEmpty()) {
            arr.add(songs.poll().getUri());
        }
        
        obj.appendField("uris", arr);

        String body_string = obj.toString();
        StringEntity entity_body_string = new StringEntity(body_string);

        put.setEntity(entity_body_string);
        client.execute(put);
    }


    public boolean getPlayStatus() throws ClientProtocolException, IOException, ParseException {

        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        HttpResponse response = client.execute(get);

        String str = HelperClass.getResponseString(response.getEntity());
        if (str.equals(null) || "".equals(str)) {
            return false;
        }

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(str);

        boolean play_status;
        play_status = (boolean) obj.get("is_playing");
        return play_status;
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
}