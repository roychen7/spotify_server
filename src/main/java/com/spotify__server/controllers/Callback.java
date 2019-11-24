/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.modules.HelperClass;
import com.spotify__server.components.listeners.SpotifyPlayerManager;
import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.components.listeners.UserManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Statement;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author roychen
 */
// 

// deals with the redirect uri while trying to authorize the spotify api
@RestController
@ComponentScan("com.spotify__server")
public class Callback {
    
    @Autowired
    private SpotifyPlayerManager server_listener;
    
    @Autowired
    private UserManager user_listener;
    
    // uses authorization code to request for access token, stores it in db once retrieved
    @RequestMapping("/callback") 
    public ResponseEntity callback(@RequestParam String code) throws MalformedURLException, JSONException, UnsupportedEncodingException, IOException, ParseException, SQLException {
        System.out.println("/callback");
        
        // initializing client and httpPost (post request is to get access token from given access code in req URL)
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://accounts.spotify.com/api/token/");
        
        // adding headers for post request
        httpPost.setHeader("Authorization", "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
        
        // adding body for post request   
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/callback"));
        params.add(new BasicNameValuePair("scope", "user-modify-playback-state"));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        // execute the post, save response
        HttpResponse response = client.execute(httpPost);
        
        // get string format of response
        HttpEntity entity = response.getEntity();
        String jsonString = HelperClass.getResponseString(entity);
        
        // parse response string into json object
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(jsonString);
        
        System.out.println("before connection");

        // insert the access token from post response into database, return a "loading" screen
        String access_token = (String) jsonObj.get("access_token");
        String refresh_token = (String) jsonObj.get("refresh_token");
        
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        
        ResultSet rset = stmt.executeQuery("select `access_token` from `token`"); 
        if (rset.next()) {
            stmt.executeUpdate("delete from `token`");
        }
        
        System.out.println("/callback after rs.next()");
        String str = "insert into `token` (`access_token`, `refresh_token`) values ('" +access_token+ "','" +refresh_token+ "')";
        stmt.executeUpdate(str);
        user_listener.updateAccessToken();
        con.close();
        
        synchronized(server_listener.test) {
            server_listener.test.notifyAll();
            System.out.println("/callback notified thread");
        }
        
        return new ResponseEntity<>("Loading...", HttpStatus.ACCEPTED);
        } catch (Error e) {
        return new ResponseEntity<>("An Error was encountered during connection to db", HttpStatus.BAD_REQUEST);
    }
    }
}