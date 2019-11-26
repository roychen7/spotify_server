package com.spotify__server.components.initializers;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import com.spotify__server.database_access.DatabaseAccesser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 *
 * @author roychen
 */

 @Component
 public class SongInitializer {
     
    private void initSongsFromPlaylistId(String playlist_id) throws SQLException, IOException, ParseException, net.minidev.json.parser.ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlist_id + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists))");
        HttpClient client = HttpClients.createDefault();
        
        get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
        
        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);
    }
 }