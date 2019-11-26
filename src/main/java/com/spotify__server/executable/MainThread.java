/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.executable;

import com.spotify__server.components.managers.SpotifyPlayerManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.parser.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// thread class responsible for initializing/scheduling time-scheduled tasks in the program
public class MainThread implements Runnable {
    
    private int play_time = 0;
    private int pause_time = 0;
    private boolean already_paused = false;
    private boolean supposed_to_pause;
    private SpotifyPlayerManager spotify_player_manager;            
    
    public MainThread(SpotifyPlayerManager spm) {
        System.out.println("main thread created!");
        play_time = pause_time = 0;
        already_paused = false;
        this.spotify_player_manager = spm;
    }
     
    public void run() {
        System.out.println("running!");
        Timer t = new Timer();
        t.schedule(new Execute(), 0, 1000);
        t.schedule(new Refresh(), 0, 3600000);
    }
    
    class Execute extends TimerTask {

        @Override
        public void run() {
            System.out.println("running!");
            if (spotify_player_manager.getPlayStatus()) {
                System.out.println("PLAY: " +play_time);
                if (already_paused) {
                    already_paused = false;
                    pause_time = 0;
                }
                
                if (play_time == 3600) {
                    spotify_player_manager.togglePlayback(false);
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
                        spotify_player_manager.togglePlayback(true);
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
    
    class Refresh extends TimerTask {
        
        public void run() {
        HttpGet get = new HttpGet("http://localhost:8080/refresh");
        HttpClient client = HttpClients.createDefault();
        
        try {
            client.execute(get);
        } catch (IOException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    }   
}
