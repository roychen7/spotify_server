package com.spotify__server.initializers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.spotify__server.components.SpotifyPlayer;
import com.spotify__server.components.SpotifyPlayerState;

public class PauserInitializer implements Initializer, Runnable {

    private int play_time = 0;
    private int pause_time = 0;
    private boolean already_paused = false;
    private boolean supposed_to_pause;
    private SpotifyPlayerState sps;  
    private SpotifyPlayer spotify_player;

    public PauserInitializer(SpotifyPlayerState sps, SpotifyPlayer spotify_player) {
        this.sps = sps;
    }

    @Override
    public void run() {
        initialize();
    }

    @Override
    public void initialize() {
        System.out.println("inside of pauserInitializer initialize method!");
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Execute(), 0, 1, TimeUnit.SECONDS);
        System.out.println("initialized pauserInitializer!");
    }

    class Execute implements Runnable {

        @Override
        public void run() {
            System.out.println("running, sps's play status is: " + sps.getPlayStatus());
            if (sps.getPlayStatus()) {
                System.out.println("PLAY: " + play_time);
                if (already_paused) {
                    already_paused = false;
                    pause_time = 0;
                }
                
                if (play_time == 3600) {
                    spotify_player.togglePlayback(false);
                    supposed_to_pause = true;
                    System.out.println("executed pause");
                    return;
                }
                
                play_time++;
            } else {
                if (!already_paused) {
                    already_paused = true;
                } else {
                    if (supposed_to_pause == true && pause_time >= 450){
                        spotify_player.togglePlayback(true);
                        play_time = 0;
                        
                        System.out.println("executed play");
                        supposed_to_pause = false;
                        return;
                    } else if (pause_time >= 300) {
                        play_time = 0;
                    }
                }
                pause_time++;
                System.out.println("PAUSE: " + pause_time);
            }
        }
    }
}