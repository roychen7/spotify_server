/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.executable;

import com.spotify__server.components.SpotifyPlayerState;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

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
    private SpotifyPlayerState sps;            
    
    public MainThread(SpotifyPlayerState sps) {
        System.out.println("main thread created!");
        play_time = pause_time = 0;
        already_paused = false;
        this.sps = sps;
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
            System.out.println("running, sps's play status is: " + sps.getPlayStatus());
            if (sps.getPlayStatus()) {
                System.out.println("PLAY: " +play_time);
                if (already_paused) {
                    already_paused = false;
                    pause_time = 0;
                }
                
                if (play_time == 3600) {
                    sps.togglePlayback(false);
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
                        sps.togglePlayback(true);
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
