/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;
import java.util.Observer;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */
@Component
public class ServerListener extends Observable {
    private int connected;
    
    public void setConnected(int a) {
        connected = a;
    }
    
    
    public int getConnected() {
        return connected;
    }
    
    @Cacheable(cacheNames = "play_status") 
    public boolean getPlayStatus() throws SQLException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.addHeader("Authorization", "Bearer " + getAccessToken());
        HttpResponse response = HttpClients.createDefault().execute(get);
        
        String str = HelperClass.getResponseString(response.getEntity());
        if (str.equals(null) || "".equals(str)) {
            return false;
        }
        
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(str);
        
        
        return (boolean) obj.get("is_playing");
    }
    
    @CachePut(cacheNames="play_status")
    public boolean updateToFalse() {
        setChanged();
        notifyObservers(false);
        return false;
    }
    
    @CachePut(cacheNames="play_status")
    public boolean updateToTrue() {
        setChanged();
        notifyObservers(true);
        return true;
    }
    
    @Cacheable(cacheNames = "getToken")
    public String getAccessToken() throws SQLException, IOException {
        System.out.println("not cached!");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
            
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
    }
    
    @CachePut(cacheNames="getToken")
    public String updateAccessToken() throws SQLException, IOException {
        System.out.println("updating access token!");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
            
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
    }
}
