/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.threads.MainThread;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
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
       
    // singular thread executor
    private Executor single_executor;
    
    // bool returning true if user has a currently playing track, false otherwise
    private boolean play;
    
    
    private GlobalSingleton() {
        try{
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
        
        }catch (IOException ex) {
                System.out.println("ioexception");
                Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                System.out.println("parseexception");
                Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    
    public static GlobalSingleton getInstance() {
        if (instance == null) {
            instance = new GlobalSingleton();
        }
        return instance;
    }

    public String getToken() {
        return access_token;
    }
    
    public void updateToken(String token) {
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
}
