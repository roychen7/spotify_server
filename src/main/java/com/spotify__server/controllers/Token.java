/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.ThreadExecutor;
import com.spotify__server.modules.HelperClass;
import com.spotify__server.components.managers.SpotifyPlayerManager;
import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.executable.MainThread;
import com.spotify__server.components.managers.UserManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

// Contains endpoints that deal with storing, retrieving, and getting access/refresh tokens
@RestController
@ComponentScan("com.spotify__server")
public class Token {
    static String access_token;
    static String refresh_token;
    
    @Autowired
    private SpotifyPlayerManager spotify_player_manager;
    
    @Autowired
    private ThreadExecutor thread_executor;

    @Autowired
    private UserManager user_manager;
    
    // returns the token stored in the database
    @GetMapping("/token")
    public ResponseEntity getToken() throws SQLException, IOException, ParseException {
        System.out.println("controllers:token/token");
        
        // allowing cross-origin access from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");

        String ress = user_manager.getAccessToken();
         
        return new ResponseEntity<>(ress, headers, HttpStatus.ACCEPTED);
    }
        
    // test mapping to update SpotifyListener's cached access token
    @GetMapping("/update")
    public void test() throws IOException, SQLException {
        System.out.println("connecting from Token::/update");
        user_manager.updateAccessToken();
    }
    
    
    // tests whether token is valid or not by calling HelperClass's verifyToken function 
    // TODO: MAY NEED TO REFACTOR LATER ON. REMOVE SINGLETON
    @GetMapping("/valid_token")
    public ResponseEntity tokenExists() throws SQLException, IOException, ParseException {
        System.out.println("controllers:token:/valid_token");
        
        // allowing cross-origin access from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            
            // grab the "access token" from the database
            String str = "select `access_token` from `token`";
            ResultSet rs = stmt.executeQuery(str);
            
            // set code to 2xx if access token is valid, default of 4xx (invalid)
            int code = 400;
            if (rs.next()) {
                code = HelperClass.verifyToken(rs.getString(1));
            }
            
            con.close();
            if (Integer.toString(code).charAt(0) == "2".charAt(0)) {
                if (spotify_player_manager.getConnected() == 0) {
                    
                    spotify_player_manager.initPlayStatus();
                    spotify_player_manager.setConnected(1);
                    
                    MainThread t1 = new MainThread(spotify_player_manager);
                    thread_executor.getInstance().execute(t1);    
                    
                    user_manager.updateProperties();
                }
            System.out.println("SERVER connected: " + spotify_player_manager.getConnected());
            }
        
             return new ResponseEntity<>(code, headers, HttpStatus.ACCEPTED);
        }
    }
    
    // use refresh token to obtain new access token and store it in the db
    @GetMapping("/refresh")
    public ResponseEntity getRefreshToken() throws SQLException, IOException, ParseException {
        
        // 1. use refresh token to access and obtain new access token
        // 2. replace the old one in the db with the new access token
        
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
        HttpClient client = HttpClients.createDefault();
        
        System.out.println("connecting from Token::/refresh");
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `refresh_token` from `token`");
        String refresh_token = "";
        if (rs.next()) {
            refresh_token = rs.getString(1);
        }
        
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", refresh_token));
        
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        post.addHeader("Authorization", "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        
        HttpResponse resp = client.execute(post);
        
        HttpEntity entity = resp.getEntity();
        String jsonString = HelperClass.getResponseString(entity);
        
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(jsonString);
        
        String access_token = (String) jsonObj.get("access_token");
        stmt.executeUpdate("update `token` set `access_token`='" +access_token+ "'");
        con.close();
        
        user_manager.updateAccessToken();
        System.out.println("Token::/refresh: updated token!");
        
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    } catch (Error e) {
        return new ResponseEntity<>("An Error was encountered during connection to db", HttpStatus.BAD_REQUEST);
    }
    }
}
