package com.spotify__server.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
// responsible for managing active spotify playback properties (eg. song, volume, etc.) and state
@Component
public class SpotifyPlayer {

    @Autowired
    private SpotifyPlayerState sps;
    
    @Autowired
    private SpotifyApiAccesser api_accesser;

    public void togglePlayback(boolean b) {
        try {
            if (b == true) {
                api_accesser.togglePlay();
                sps.setPlayStatus(true);
                return;
            }

            api_accesser.togglePause();
            sps.setPlayStatus(false);
            
            } catch (ClientProtocolException ce) {
                ce.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
        }       
    }

    public void playSong(String song_uri) {
        try {
            api_accesser.playSong(song_uri);
            sps.setPlayStatus(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}