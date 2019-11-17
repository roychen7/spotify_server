/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.modules.GlobalSingleton;
import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.minidev.json.parser.ParseException;
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

// this class is meant for dealing with functionalities regarding playing/pausing/skipping songs
@RestController
public class Player {
    
    // mapping for pausing current song
    @GetMapping("/pause")
    public ResponseEntity pauseSong() throws SQLException, IOException, ParseException {
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
        
        GlobalSingleton.getInstance().updatePlay(false);
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/pause_upd")
    public ResponseEntity updatePauseSingleton() throws IOException, ParseException {
        GlobalSingleton.getInstance().updatePlay(false);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }
    
    // mapping for playing current song
    @GetMapping("/play")
    public ResponseEntity playSong() throws SQLException, IOException, ParseException {
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
        
        GlobalSingleton.getInstance().updatePlay(true);
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/play_upd")
    public ResponseEntity updatePlaySingleton() throws IOException, ParseException {
        GlobalSingleton.getInstance().updatePlay(true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }
}
