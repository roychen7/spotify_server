package com.spotify__server.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spotify__server.database_access.DatabaseAccesser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

// responsible for managing active spotify playback properties (eg. song, volume, etc.) and state
@Component
public class SpotifyPlayer {

    @Autowired
    private SpotifyPlayerState sps;

    public void togglePlayback(boolean b) {
        HttpClient client = HttpClients.createDefault();
        HttpPut put;

        if (b == true) {
            put = new HttpPut("https://api.spotify.com/v1/me/player/play");
        } else {
            put = new HttpPut("https://api.spotify.com/v1/me/player/pause");
        }

        try {
            String access_token = DatabaseAccesser.getAccessToken();
            put.addHeader("Authorization", "Bearer " + access_token);

            client.execute(put);

            if (b == true) {
                sps.setPlayStatus(true);
            } else {
                sps.setPlayStatus(false);
            }

        } catch (IOException ex) {
            Logger.getLogger(SpotifyPlayerState.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public boolean playSong(String song_uri) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
            
            put.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("context_uri", song_uri));
            
            put.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            
            client.execute(put);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}