/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */
@RestController
public class Player {
    
    // mapping for pausing current song
    @GetMapping("/pause")
    public ResponseEntity pauseSong() throws SQLException, IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/pause");
        
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        String s = "";
        
        if (rs.next()) {
            s = rs.getString(1);
        }
        
        put.addHeader("Authorization", "Bearer " + s);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        HttpResponse response = client.execute(put);
        
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
    }
    
    // mapping for playing current song
    @GetMapping("/play")
    public ResponseEntity playSong() throws SQLException, IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
        
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        String s = "";
        
        if (rs.next()) {
            s = rs.getString(1);
        }
        
        put.addHeader("Authorization", "Bearer " + s);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        HttpResponse response = client.execute(put);
        
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
    }
}
