package com.spotify__server.components.accessers.spotify_api_access;

import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.modules.Artist;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.HelperClass;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


import java.io.IOException;

@Component
public class GetInfoApi {

    @Autowired
    private DatabaseAccesser database_accesser;

    private HttpClient client = HttpClients.createDefault();

    // accesses spotify API to return user ID using access token
    public String getUserId() throws ClientProtocolException, IOException, ParseException {
        System.out.println("playlist_database_accesser::getUserId");
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");

        // grab access token and add it to get header
        String access_token = database_accesser.getAccessToken();
        System.out.println(access_token);
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

    // returns new song with the uri, name, and duration
    public Song getSongDetails(String song_id) throws org.apache.http.ParseException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/tracks/" + song_id);
        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        System.out.println(database_accesser.getAccessToken());

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

    // returns play status of user's playback. True if playing, false if paused, and null if 
    // there is no active playback state
    public Boolean getPlayStatus() throws ClientProtocolException, IOException, ParseException {

        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        System.out.println(database_accesser.getAccessToken());
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

    // returns the artists associated with the given song 
	public List<Artist> getSongArtists(String song_id) throws ClientProtocolException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/tracks/" + song_id);
        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);

        List<Artist> ret_list = new ArrayList<>();
        JSONArray artists = (JSONArray) obj.get("artists");
        for (int i = 0; i < artists.size(); i++) {
            ret_list.add(new Artist(((JSONObject)artists.get(i)).getAsString("id"), ((JSONObject)artists.get(i)).getAsString("name")));
        }
        
        return ret_list;
	}
}