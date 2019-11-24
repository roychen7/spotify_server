/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.spotify__server.modules.HelperClass;
import com.spotify__server.components.listeners.SpotifyPlayerManager;
import com.spotify__server.components.listeners.UserManager;
import com.spotify__server.repositories.JdbcRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author roychen
 */

// class that deals with the login endpoint
@RestController
@ComponentScan("com.spotify__server")
public class Login {
    
    
    @Autowired
    private SpotifyPlayerManager server_listener;
    
    @Autowired
    private UserManager user_listener;
    
    
    @GetMapping("/test")
    public ResponseEntity testlol() throws IOException, SQLException, ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        String s = user_listener.getUserId();
            HttpGet get = new HttpGet("https://api.spotify.com/v1/users/" + s + "/playlists");
            get.addHeader("Authorization", "Bearer " + user_listener.getAccessToken());
            HttpClient client = HttpClients.createDefault();
            
            HttpResponse http_response = client.execute(get);
            String response_string = EntityUtils.toString(http_response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject json_response_object = (JSONObject) parser.parse(response_string);
            String ret = (String) json_response_object.getAsString("items");
            
            JSONArray items_array = (JSONArray) json_response_object.get("items");
            
            
        
        return new ResponseEntity<>(ret, headers, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/testlol")
    public ResponseEntity testloll() throws SQLException, IOException {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery("Show tables");
            String res = "";
            while (rs.next()) {
                res = res + rs.getString(1);
            }
            
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }
    }
    
    // If no valid token is stored in the database, waits on a test object. Once awoken, will double-check condition and return code 200
    // if valid token is now stored in database
    @GetMapping("/login")
    public ResponseEntity login() throws MalformedURLException, IOException, InterruptedException, SQLException {
        
        // setting headers to allow access from electron application from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        // getting connection to mysql database
        
        // loops to check if valid access token is inside mysql table, if valid then return valid access code (eg. 200),
        // if invalid token is given then loop breaks and returns invalid access code (eg. 400)a
//        while (true) {
////            System.out.println("INSIDE LOOP");
//        ResultSet rs = stmt.executeQuery(str);
//        String s = "";
//        if (rs.next()) {
////            System.out.println("Inside IF STATEMENT INSIDE LOOP ");
//            s = rs.getString(1);
////            System.out.println(s);
//            int resp = HelperClass.verifyToken(s);
//            String temp = Integer.toString(resp);
//            if (temp.charAt(0) == "2".charAt(0)) {
//                con.close();
//                return new ResponseEntity<>(resp, headers, HttpStatus.ACCEPTED);
//        }
//        }
//        Thread.sleep(500);
//        }
        int responseCode;
        synchronized (server_listener.test) {
            while (Integer.toString(responseCode = HelperClass.verifyToken(user_listener.getAccessToken())).charAt(0) != "2".charAt(0)) {
                System.out.println("/login right before waiting");
                server_listener.test.wait();
                System.out.println("/login awoke from waiting");
            }
            System.out.println("/login before returning");
            return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
        }
//        return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
    }   
}
