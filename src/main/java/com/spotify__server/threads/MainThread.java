/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.threads;

import com.spotify__server.modules.GlobalSingleton;
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

/**
 *
 * @author roychen
 */
public class MainThread implements Runnable {
    
    private int play_time;
    private int pause_time;
    private boolean already_paused;
    private HttpClient client;
    private HttpGet get_pause;
    private HttpGet get_play;
            
    public MainThread() {
        System.out.println("main thread created!");
        play_time = pause_time = 0;
        already_paused = false;
        
        client = HttpClients.createDefault();
        get_pause = new HttpGet("http://localhost:8080/pause");
        get_play = new HttpGet("http://localhost:8080/play");
    }
     
    public void run() {
        System.out.println("running!");
        Timer t = new Timer();
        t.schedule(new Execute(), 0, 1000);
        t.schedule(new Refresh(), 0, 3600000);
        while (true) {
            
        }
    }
    
    class Execute extends TimerTask {

        @Override
        public void run() {
            try {
                if (GlobalSingleton.getInstance().getPlay() == true) {
                    System.out.println("PLAY: " +play_time);
                    if (already_paused) {
                        already_paused = false;
                        pause_time = 0;
                    }
                                        
                    if (play_time == 3600) {
                        client.execute(get_pause);
                        GlobalSingleton.getInstance().updateSupposedToPause(true);
                        System.out.println("executed pause");
                        return;
                    }
                    
                    play_time++;
                } else {
                    if (!already_paused) {
                        already_paused = true;
                    } else {
                        if (GlobalSingleton.getInstance().getSupposedToPause() == true && pause_time == 600){
                                  play_time = 0;
                                  client.execute(get_play);
                                  
                                  System.out.println("executed play");
                                  GlobalSingleton.getInstance().updateSupposedToPause(false);
                                  return;
                          } else if (pause_time >= 450) {
                              play_time = 0;
                          }
                    }
                    pause_time++;
                    System.out.println("PAUSE: " + pause_time);
                }
            } catch (IOException ex) {
                System.out.println("ioexception");
                Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
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
