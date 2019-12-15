/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import com.spotify__server.components.managers.SpotifyPlayer;
import com.spotify__server.components.managers.SpotifyPlayerState;
import com.spotify__server.modules.Song;

import java.io.IOException;
import java.util.Queue;

/**
 *
 * @author roychen
 */
public class PlaylistGenerator {
    private SpotifyPlayer spotify_player;
    private SpotifyPlayerState sps;
    
    public PlaylistGenerator(SpotifyPlayer spotify_player, SpotifyPlayerState sps) {
        this.sps = sps;
        this.spotify_player = spotify_player;
    }
    
    public void initPlaying() throws IOException {
        Queue<Song> songs = sps.getPlayQueue();
        spotify_player.playSongs(songs);
    }
}
