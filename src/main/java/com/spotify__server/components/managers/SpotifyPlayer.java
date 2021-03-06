package com.spotify__server.components.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;
import com.spotify__server.modules.Song;

import java.io.IOException;
import java.util.Queue;

import org.apache.http.client.ClientProtocolException;
// responsible for managing active spotify playback state
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

    public void playSongs(Queue<Song> songs) {
        try {
            api_accesser.playSongs(songs);
            sps.setPlayStatus(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}