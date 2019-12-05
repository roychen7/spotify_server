/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import com.spotify__server.components.SpotifyPlayerState;
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
    private SpotifyPlayerState sps;
    private TimerTask timer_task;
    
    public PlaylistGenerator(SpotifyPlayerState sps) {
        this.play_queue = play_queue;
        this.sps = sps;
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
        sps.playSong(s.getUri());
        
        timer.schedule(timer_task, s.getDuration());
    }
}
