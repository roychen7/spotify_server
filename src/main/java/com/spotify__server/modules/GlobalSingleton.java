/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author roychen
 */

// Singleton for storing data related to database, and arbitrary elements such as the executor 
public class GlobalSingleton {
    private static GlobalSingleton instance;
    private String access_token;
    
    // 
    private Executor single_executor;
    
    // bool returning true if user has a currently playing track, false otherwise
    private boolean play;
    
    // bool for determining if user can currently play (might be disallowed if user reached time limit)
    private boolean can_play;
    
    // bool for determining if program is supposed to auto-pause. Used in mainthread
    private boolean supposed_to_pause;
    
    
    private GlobalSingleton() throws IOException, ParseException {
        HttpGet get = new HttpGet("http://localhost:8080/token");
        HttpClient client = HttpClients.createDefault();
        
        HttpResponse response = client.execute(get);
        
        access_token = EntityUtils.toString(response.getEntity());
        single_executor = Executors.newSingleThreadExecutor();
        
        get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.addHeader("Authorization", "Bearer " + access_token);
        response = client.execute(get);
        
        String str = HelperClass.getResponseString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(str);
        
        play = (boolean) obj.get("is_playing");
        System.out.println("IS PLAYING IS : " + obj.get("is_playing"));
        can_play = true;
        supposed_to_pause = false;
    }
    
    public static GlobalSingleton getInstance() throws IOException, ParseException {
        if (instance == null) {
            instance = new GlobalSingleton();
        }
        return instance;
    }
    
    public boolean getSupposedToPause() {
        return supposed_to_pause;
    }
    
    public void updateSupposedToPause(boolean b) {
        this.supposed_to_pause = b;
    }
    public String getToken() {
        return access_token;
    }
    
    public void updateToken(String token) throws IOException, ParseException {
        access_token = token;
    }
    
    public Executor getExecutor() {
        return single_executor;
    }
    
    public boolean getPlay() {
        return play;
    }
    
    public void updatePlay(boolean play) {
        this.play = play;
    }
    
    public boolean getCanPlay() {
        return can_play;
    }
    
    public void setCanPlay(boolean can_play) {
        this.can_play = can_play;
    }
}
