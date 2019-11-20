/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.modules.GlobalSingleton;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spotify__server.repositories.JdbcRepository;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.spotify__server.modules.HelperClass;
import com.spotify__server.modules.ServerListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author roychen
 */

// this class deals with the login endpoint
@RestController
@ComponentScan("com.spotify__server")
public class Login {
    
    
    @Autowired
    ServerListener server_listener;
    
    
    @GetMapping("/testloginlistener")
    public void testlol() {
        System.out.println(server_listener.getConnected());
    }
    
    // returns code 200 once a valid token is stored in the database. will be in endless loop until this happens
    @GetMapping("/login")
    public ResponseEntity login() throws MalformedURLException, IOException, InterruptedException, SQLException {
        
        // setting headers to allow access from electron application from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        // getting connection to mysql database
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        String str = "select `access_token` from `token`";
//        System.out.println("BEFORE LOOP");
        
        // loops to check if valid access token is inside mysql table, if valid then return valid access code (eg. 200),
        // if invalid token is given then loop breaks and returns invalid access code (eg. 400)a
        while (true) {
//            System.out.println("INSIDE LOOP");
        ResultSet rs = stmt.executeQuery(str);
        String s = "";
        if (rs.next()) {
//            System.out.println("Inside IF STATEMENT INSIDE LOOP ");
            s = rs.getString(1);
//            System.out.println(s);
            int resp = HelperClass.verifyToken(s);
            String temp = Integer.toString(resp);
            if (temp.charAt(0) == "2".charAt(0)) {
                con.close();
                return new ResponseEntity<>(resp, headers, HttpStatus.ACCEPTED);
        }
        }
        Thread.sleep(500);
        }
    } catch (Error e) {
        return new ResponseEntity<>("An Error was encountered during connection to db", HttpStatus.BAD_REQUEST);
    }
    }
}
