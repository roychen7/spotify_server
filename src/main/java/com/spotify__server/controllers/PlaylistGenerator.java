/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

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
public class PlaylistGenerator {
    private int i = 0;
    
    @GetMapping("/testgenerator")
    public ResponseEntity getWeightedPlaylistMixSongs() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        if (i == 0) {
            System.out.println("resetting i to value: " + i);
            i = 1;
            return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
        }
        
        if (i == 1) {
            i = 2;
        }
        
        return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
    }
}
