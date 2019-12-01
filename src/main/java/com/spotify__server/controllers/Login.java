/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.ThreadExecutor;
import com.spotify__server.components.initializers.PlaylistInitializer;
import com.spotify__server.components.initializers.SongInitializer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.spotify__server.utils.HelperClass;
import com.spotify__server.components.managers.SpotifyPlayerManager;
import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.executable.MainThread;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author roychen
 */

// class that deals with the login endpoint
@RestController
@ComponentScan("com.spotify__server")
public class Login {
    
    @Autowired
    private ThreadExecutor thread_executor;
    
    @Autowired
    private SpotifyPlayerManager spotify_player_manager;  
    
    @GetMapping("/test")
    public ResponseEntity testlol() throws IOException, SQLException, ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        List<String> list = new ArrayList<>();
        list.add("hi");
        list.add("1");
        list.add("two");
        return new ResponseEntity<>(list, headers, HttpStatus.ACCEPTED);
    }
   
   
    @RequestMapping(value="/testgenerator", method={RequestMethod.POST}, consumes = "application/x-www-form-urlencoded")
    public ResponseEntity getWeightedPlaylistMixSongs(@RequestBody String songs) {
        System.out.println("Login::/testgenerator");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000"); 
            
        return new ResponseEntity<>("asdf", headers, HttpStatus.ACCEPTED);
        }
    
    // thread sleeps until awoken, and checks if token in db is valid, returns code 2xx if it is, if invalid, then sleeps again and repeats process
    @GetMapping("/login")
    public ResponseEntity login() throws MalformedURLException, IOException, InterruptedException, SQLException {
        
        // setting headers to allow access from electron application from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        int responseCode;
        synchronized (spotify_player_manager.test) {
            while (Integer.toString(responseCode = HelperClass.verifyToken(DatabaseAccesser.getAccessToken())).charAt(0) != "2".charAt(0)) {
                System.out.println("/login right before waiting");
                spotify_player_manager.test.wait();
                System.out.println("/login awoke from waiting");
            }
            
            MainThread t1 = new MainThread(spotify_player_manager);
                    thread_executor.getInstance().execute(t1);    
                    
                    Thread t2 = new Thread(new PlaylistInitializer(new SongInitializer()));
                    t2.start();
            System.out.println("/login before returning, code is: " + responseCode);
            return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
        }
//        return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
    }   
}
