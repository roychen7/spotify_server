package com.spotify__server.components.accessers.spotify_api_access;

// package dependencies
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.components.data.Data;
import com.spotify__server.modules.Song;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;


import java.io.IOException;

//TODO: refactor this entire class into the 3 api accesser classes
@Component
public class SpotifyApiAccesser {

    @Autowired
    private DatabaseAccesser database_accesser;

    private HttpClient client = HttpClients.createDefault();

    // toggles user playback to play mode
    public void togglePlay() throws ClientProtocolException, IOException {
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");

        String access_token = database_accesser.getAccessToken();
        put.addHeader("Authorization", "Bearer " + access_token);
        System.out.println(database_accesser.getAccessToken());
        client.execute(put);
    }

    // toggles user playback to pause mode
    public void togglePause() throws ClientProtocolException, IOException {
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/pause");

        String access_token = database_accesser.getAccessToken();
        System.out.println(database_accesser.getAccessToken());
        put.addHeader("Authorization", "Bearer " + access_token);

        client.execute(put);
    }

    // playes a queue of songs
    public void playSongs(Queue<Song> songs) throws ClientProtocolException, IOException {
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
        put.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
        System.out.println(database_accesser.getAccessToken());
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
}