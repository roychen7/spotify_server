/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.modules.HelperClass;
import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.threads.RefreshThread;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

@RestController
public class Token {
    
    @GetMapping("/token")
    public ResponseEntity tokenExists() throws SQLException, IOException {
        System.out.println("controllers:token:/token");
        
        // allowing cross-origin access from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        
        // grab the "access token" from the database
        String str = "select `access_token` from `token`";
        ResultSet rs = stmt.executeQuery(str);
        
        // set code to 2xx if access token is valid, 4xx if invalid
        int code = 400;
        if (rs.next()) {
            code = HelperClass.verifyToken(rs.getString(1));
        } 
        
        if (Integer.toString(code).charAt(0) == "2".charAt(0)) {
            ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            exec.scheduleAtFixedRate(new RefreshThread(), 0, 60, TimeUnit.MINUTES);
        }
        
        return new ResponseEntity<>(code, headers, HttpStatus.ACCEPTED);
    }  
    
    @GetMapping("/refresh")
    public ResponseEntity getRefreshToken() throws SQLException, IOException, ParseException {
        
        // 1. use refresh token to access and obtain new access token
        // 2. replace the old one in the db with the new access token
        
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
        HttpClient client = HttpClients.createDefault();
        
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        
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
        
        System.out.println("updated token!");
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }
}
