/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.managers.SpotifyPlayerState;
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.spotify_api_access.GetInfoApi;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.parser.ParseException;

/**
 *
 * @author roychen
 */

// this class is meant for dealing with functionalities regarding
// playing/pausing/skipping songs
@RestController
public class PlayerController {

    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private DatabaseAccesser database_accesser;

    @Autowired
    private GetInfoApi info_api_accesser;

    @ResponseBody
    @GetMapping("toggle_playback")
    public String togglePlayback(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        Boolean is_playing;
        System.out.println("PlayerController::togglePlayback()");
        try {
            if ((is_playing = info_api_accesser.getPlayStatus()) != null) {
                System.out.println("PlayerController::togglePlayback() after is_playing " + is_playing.toString());
                if (is_playing) {
                    api_accesser.togglePause();
                    System.out.println("PlayerController::togglePlayback() api_accesser toggle pause");
                } else {
                    api_accesser.togglePlay();
                    System.out.println("PlayerController::togglePlayback() api_accesser toggle play");
                }
            }
            return "OK";
        } catch (IOException | ParseException e) {
            return "An exception was caught! " + e.toString();
        }
    }

    // mapping for pausing current song
    @ResponseBody @GetMapping("/pause")
    public String pauseSong(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/pause");
            
            put.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
            System.out.println(database_accesser.getAccessToken());
            client.execute(put);
            
            sps.setPlayStatus(false);

            return "OK";
        } catch (IOException ex) {
            return "An error occured " + ex.getMessage();
        }
    }
    
    @ResponseBody @GetMapping("/playstatus") 
    public String getPlayStatus(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        return Boolean.toString(sps.getPlayStatus());
    }
    
    // mapping for playing current song
    @ResponseBody @GetMapping("/play")
    public String playSong(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        System.out.println("PlayerController::playSong() beginning");
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
            
            put.addHeader("Authorization", "Bearer " + database_accesser.getAccessToken());
            System.out.println(database_accesser.getAccessToken());
            client.execute(put);
            sps.setPlayStatus(true);

            return "OK";
        } catch (IOException ex) {
            return "An error occured " + ex.getMessage();
        }
    }
}
