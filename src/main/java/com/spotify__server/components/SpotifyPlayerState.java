/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components;

import com.spotify__server.utils.HelperClass;
import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.Song;
import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// responsible for managing active spotify playback properties (eg. song, volume, etc.) and state
@Component
public class SpotifyPlayerState {
    private int connected;
    public final String test = "";
    private boolean play_status;
    private PlaylistGenStatus pgs;
    private Queue<Song> play_queue;
    HashSet<String> completed_playlists;

    public void initPlayStatus() {
        
        try {
            pgs = PlaylistGenStatus.FALSE;
            completed_playlists = new HashSet<>();
            System.out.println("inside init playstatus!");
            HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
            get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
            HttpResponse response = HttpClients.createDefault().execute(get);

            String str = HelperClass.getResponseString(response.getEntity());
            if (str.equals(null) || "".equals(str)) {
                play_status = false;
                return;
            }
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(str);

            play_status = (boolean) obj.get("is_playing");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean getPlayStatus() {
        return play_status;
    }

    public void setPlayStatus(boolean b) {
        this.play_status = b;
    }

    public int getConnected() {
        return connected;
    }

    public void setConnected(int a) {
        connected = a;
    }

    public void setPlaylistGeneratorStatus(PlaylistGenStatus pgs) {
        this.pgs = pgs;
    }

    public Queue<Song> getPlayQueue() {
        return play_queue;
    }

    public void setPlayQueue(Queue<Song> play_queue) {
        this.play_queue = play_queue;
    }

    public HashSet<String> getCompletedPlaylists() {
        return completed_playlists;
    }
}
