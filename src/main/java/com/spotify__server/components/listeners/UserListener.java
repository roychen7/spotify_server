/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components.listeners;

import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// deals with listening to user information (eg. access token, user id, user playlists, etc.)
@Component
public class UserListener {
    
    public void updateProperties() {
        try {
            String s = getUserId();
            HttpGet get = new HttpGet("https://api.spotify.com/v1/users/" + s + "/playlists");
            get.addHeader("Authorization", "Bearer " + getAccessToken());
            HttpClient client = HttpClients.createDefault();
            
            HttpResponse http_response = client.execute(get);
            String response_string = EntityUtils.toString(http_response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject json_response_object = (JSONObject) parser.parse(response_string);
            
            System.out.println(json_response_object.get("items").getClass());
            
            
        } catch (SQLException ex) {
            Logger.getLogger(UserListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(UserListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String getUserId() throws SQLException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me");
        HttpClient client = HttpClients.createDefault();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        JSONParser parser = new JSONParser();
        
        String access_token = getAccessToken();
        get.addHeader("Authorization", "Bearer " + access_token);
        HttpResponse resp = client.execute(get);
        HttpEntity entity = resp.getEntity();
        String s = EntityUtils.toString(entity);
//        System.out.println("UserListener::getUserId's access token: " + s);
        JSONObject jsonObj = (JSONObject) parser.parse(s);
        
        return jsonObj.getAsString("id");
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
