/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.modules.GlobalSingleton;
import com.spotify__server.modules.ServerListener;
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
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private ServerListener server_listener;
    
    // mapping for pausing current song
    @GetMapping("/pause")
    public ResponseEntity pauseSong() throws SQLException, IOException, ParseException {
        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/pause");
        
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        String s = "";
        
        if (rs.next()) {
            s = rs.getString(1);
        }
        con.close();
        
        put.addHeader("Authorization", "Bearer " + s);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        HttpResponse response = client.execute(put);
        
        server_listener.updateToFalse();
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
     } catch (Error e) {
        return new ResponseEntity<>("An Error was encountered during connection to db", HttpStatus.BAD_REQUEST);
    }
    }
    
    @GetMapping("/pause_upd")
    public ResponseEntity pauseUpd() {
        
        System.out.println("Player.js://pause_upd");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        server_listener.updateToFalse();
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }
    // mapping for playing current song
    @GetMapping("/play")
    public ResponseEntity playSong() throws SQLException, IOException, ParseException {
        HttpClient client = HttpClients.createDefault();
        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
        
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        
        String s = "";
        
        if (rs.next()) {
            s = rs.getString(1);
        }
        
        con.close();
        put.addHeader("Authorization", "Bearer " + s);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        HttpResponse response = client.execute(put);
        
        server_listener.updateToTrue();
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
        } catch (Error e) {
        return new ResponseEntity<>("An Error was encountered during connection to db", HttpStatus.BAD_REQUEST);
    }
    }
    
    @GetMapping("/play_upd")
    public ResponseEntity playUpd() {
        System.out.println("Player.js://play_upd");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        server_listener.updateToTrue();
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }
}
