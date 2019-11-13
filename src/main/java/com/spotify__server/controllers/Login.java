/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

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
import modules.VerifyToken;
import org.apache.http.HttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author roychen
 */
@RestController
public class Login {
    
    // returns the code 2xx if access token in mysql database is valid, 4xx if invalid or doesn't exist
    @GetMapping("/token")
    public ResponseEntity tokenExists() throws SQLException, IOException {
        // allowing cross-origin access from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        
        // grab the "access token" from the database
        String str = "select * from `token` limit 1";
        ResultSet rs = stmt.executeQuery(str);
        
        if (rs.next()) {
            int code = VerifyToken.verifyToken(rs.getString(1));
            return new ResponseEntity<>(code, headers, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("400", headers, HttpStatus.ACCEPTED);
        }
    }   
    
    @GetMapping("/login")
    public ResponseEntity login() throws MalformedURLException, IOException, InterruptedException, SQLException {
        
        // setting headers to allow access from electron application from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        // getting connection to mysql database
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        String str = "select * from `token` limit 1";
        System.out.println("BEFORE LOOP");
        
        // loops to check if valid access token is inside mysql table, if valid then return valid access code (eg. 200),
        // if invalid token is given then loop breaks and returns invalid access code (eg. 400)a
        while (true) {
            System.out.println("INSIDE LOOP");
        ResultSet rs = stmt.executeQuery(str);
        String s = "";
        if (rs.next()) {
            System.out.println("Inside IF STATEMENT INSIDE LOOP ");
            s = rs.getString(1);
            System.out.println(s);
            int resp = VerifyToken.verifyToken(s);
            return new ResponseEntity<>(resp, headers, HttpStatus.ACCEPTED);
        }
        Thread.sleep(500);
        }
    }
}
