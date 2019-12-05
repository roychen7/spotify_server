/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import com.spotify__server.components.SpotifyPlayer;
import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author roychen
 */
public class PlaylistGenerator {
    private Queue<Song> play_queue;
    private Timer timer;
    private SpotifyPlayer spotify_player;
    private TimerTask timer_task;
    
    public PlaylistGenerator(SpotifyPlayer spotify_player) {
        this.play_queue = play_queue;
        this.spotify_player = spotify_player;
        timer = new Timer();
        timer_task = new TimerTask() {
            public void run() {
                try {
                    initPlaying();
                } catch (IOException ex) {
                    System.out.println("Failed to play song");
                }
            }
        };
    }
    
    public void initPlaying() throws IOException {
        Song s = play_queue.poll();
        spotify_player.playSong(s.getUri());
        
        timer.schedule(timer_task, s.getDuration());
    }
}
