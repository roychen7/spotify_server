/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components.managers;

import com.spotify__server.modules.HelperClass;
import com.spotify__server.database_access.DatabaseAccesser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// responsible for managing active spotify playback properties (eg. song, volume, etc.)
@Component
public class SpotifyPlayerManager {
    private int connected;
    public final String test = "";
    private boolean play_status;
    
    public void setConnected(int a) {
        connected = a;
    }
    
    
    public int getConnected() {
        return connected;               
    }
    
    public void initPlayStatus() throws SQLException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
        HttpResponse response = HttpClients.createDefault().execute(get);
        
        String str = HelperClass.getResponseString(response.getEntity());
        if (str.equals(null) || "".equals(str)) {
            play_status = false;
            return;
        }
        
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(str);
        
        play_status = (boolean) obj.get("is_playing");
    }
    
    public boolean getPlayStatus() {
        return play_status;
    }
    
    public void setPlayStatus(boolean b) {
        this.play_status = b;
    }
    
    public void togglePlayback(boolean b) {
        HttpClient client = HttpClients.createDefault();
        HttpPut put;
        
        if (b== true) {
            put = new HttpPut("https://api.spotify.com/v1/me/player/play");
        } else {
            put = new HttpPut("https://api.spotify.com/v1/me/player/pause");
        }
               
        try {
            String access_token = DatabaseAccesser.getAccessToken();
            put.addHeader("Authorization", "Bearer " + access_token);

            HttpResponse response = client.execute(put);
                    
            if (b== true) {
                play_status = true;
            } else {
                play_status = false;
            } 
            
            } catch (IOException ex) {
                Logger.getLogger(SpotifyPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(SpotifyPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
