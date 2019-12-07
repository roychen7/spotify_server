/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.SpotifyPlayerState;
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

// this class is meant for dealing with functionalities regarding playing/pausing/skipping songs
@RestController
public class PlayerController {
    
    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private DatabaseAccesser database_accesser;
        
    // mapping for pausing current song
    @GetMapping("/pause")
    public ResponseEntity pauseSong() {
        HttpHeaders headers = null; 
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/pause");
            
            put.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
            
            headers = new HttpHeaders();
            headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
            
            HttpResponse response = client.execute(put);
            
            sps.setPlayStatus(false);
            return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
        } catch (IOException ex) {
            return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
        }
    }
    
    
    // mapping for updating MainThread's playStatus boolean to false through calling SpotifyListener's function
    @GetMapping("/pause_upd")
    public ResponseEntity pauseUpd() {
        
        System.out.println("Player.js://pause_upd");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        sps.setPlayStatus(false);
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/playstatus") 
    public ResponseEntity getPlayStatus() {
        return new ResponseEntity<>(Boolean.toString(sps.getPlayStatus()), HttpStatus.ACCEPTED);
    }
    
    // mapping for playing current song
    @GetMapping("/play")
    public ResponseEntity playSong() {

        System.out.println("PlayerController::playSong() beginning");

        HttpHeaders headers = null;
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
            
            System.out.println("PlayerController::playSong() before getAccessToken");
            put.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
            System.out.println("PlayerController::playSong() after getAccessToken");

            headers = new HttpHeaders();
            headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
            
            System.out.println("PlayerController::playSong() before execute put");
            HttpResponse response = client.execute(put);
            System.out.println("PlayerController::playSong() after execute put");
            
            sps.setPlayStatus(true);
            System.out.println("PlayerController::playSong() returning");
            return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
        } catch (IOException ex) {
            System.out.println("PlayerController::playSong() caught IOException");
            return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
        }
    }
    
    // mapping for updating MainThread's playStatus boolean to true through calling SpotifyListener's function
    @GetMapping("/play_upd")
    public ResponseEntity playUpd() {
        System.out.println("Player.js://play_upd");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        sps.setPlayStatus(true);
        return new ResponseEntity<>("", headers, HttpStatus.ACCEPTED);
    }
}
