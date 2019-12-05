package com.spotify__server.utils;

import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.repositories.JdbcRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import javafx.util.Pair;
import org.springframework.http.HttpHeaders;
import com.spotify__server.utils.SpotifyApiCaller;
import java.util.HashSet;

public class SpotifyApiCaller {

    public static String getUserId() throws ClientProtocolException, IOException, ParseException {

        HttpGet get = new HttpGet("https://api.spotify.com/v1/me");
        HttpClient client = HttpClients.createDefault();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");

        // grab access token and add it to get header
        String access_token = DatabaseAccesser.getAccessToken();
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

    public static List<Pair<String, String>> getAndUpdatePlaylistIdsAndNames(String user_id)
            throws ClientProtocolException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/users/" + user_id + "/playlists");
        get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
        List<Pair<String, String>> ret_list = new ArrayList<>();

        HttpClient client = HttpClients.createDefault();
        HttpResponse http_response = client.execute(get);
        String response_string = EntityUtils.toString(http_response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject json_response_object = (JSONObject) parser.parse(response_string);

        System.out.println(json_response_object.get("items").getClass());
        JSONArray resp_array = (JSONArray) json_response_object.get("items");

        for (int i = 0; i < resp_array.size(); i++) {
            JSONObject obj = (JSONObject) resp_array.get(i);
            ret_list.add(new Pair(obj.getAsString("id"), obj.getAsString("name")));

            DatabaseAccesser.insertIntoDb("insert ignore into `playlists` set `playlist_id`='"
                    + ret_list.get(i).getKey() + "', `playlist_name`='" + ret_list.get(i).getValue() + "'");
        }

        DatabaseAccesser.updatePlaylistNames();
        return ret_list;
    }

    public static void updatePlaylistSongs(String playlist_id, int noOfResets)
            throws ClientProtocolException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlist_id
                + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists%2Curi))");
        HttpClient client = HttpClients.createDefault();

        get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());

        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);
        JSONArray items_array = (JSONArray) obj.get("items");

        HashSet<String> existing_song_ids = DatabaseAccesser.getExistingSongs();

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

            if (!existing_song_ids.contains(song_id)) {
                if ((ind_of_quote = song_name.indexOf("'")) == -1) {
                    insert_string = "insert ignore into `songs` set `playlist_id`='" + playlist_id + 
                    "', `song_id`='" + song_id + "', `song_name`='" + song_name + 
                    "', `song_duration`='" + song_duration + "'";
                } else {
                    String song_name_reformatted = handleQuotation(song_name, ind_of_quote);

                    insert_string = "insert ignore into `songs` set `playlist_id`='" + playlist_id + 
                    "', `song_id`='" + song_id + "', `song_name`='" + song_name_reformatted + 
                    "', `song_duration`='" + song_duration + "'";
                }
            }

            if (!insert_string.equals("")) {
                DatabaseAccesser.insertIntoDb(insert_string);
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
                get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());

                response = client.execute(get);
                response_string = EntityUtils.toString(response.getEntity());
                obj = (JSONObject) parser.parse(response_string);
                items_array = (JSONArray) obj.get("items");
                System.out.println("items_array size: " + items_array.size());
            }
        }
    }

    public static void refreshToken() throws FileNotFoundException, SQLException, IOException, ParseException {
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
        HttpClient client = HttpClients.createDefault();

        String refresh_token = DatabaseAccesser.getSingleFromDb("select `refresh_token` from `token`");
        
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", refresh_token));
        
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        post.addHeader("Authorization", "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        
        HttpResponse resp = client.execute(post);
        
        HttpEntity entity = resp.getEntity();
        String jsonString = HelperClass.getResponseString(entity);
        
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(jsonString);
        
        String access_token = (String) jsonObj.get("access_token");
        DatabaseAccesser.insertIntoDb("update `token` set `access_token`='" + access_token + "'");
        DatabaseAccesser.updateAccessToken();
        System.out.println("Token::/refresh: updated token!");
    }

    // append a second "'" character everytime we encounter one 
    private static String handleQuotation(String song_name, int ind_of_quote) {  
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