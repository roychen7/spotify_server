/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components;

import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;
import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.Song;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// responsible for managing active spotify playback properties (eg. song, volume, etc.) and state
@Component
public class SpotifyPlayerState {
    
    @Autowired
    private SpotifyApiAccesser api_accesser;

    private int connected;
    public final String test = "";
    private boolean play_status;
    private PlaylistGenStatus pgs;
    private Queue<Song> play_queue;
    private HashSet<String> completed_playlists;

    public void initPlayStatus() {
        
        completed_playlists = new HashSet<>();
        try {
            play_status = api_accesser.getPlayStatus();
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
