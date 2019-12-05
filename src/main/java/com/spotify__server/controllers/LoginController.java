/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.initializers.InitializeAll;
import com.spotify__server.initializers.PlaylistAndSongInitializer;
import java.io.IOException;
import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.spotify__server.utils.HelperClass;
import com.spotify__server.components.SpotifyPlayerState;
import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.executable.MainThread;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.parser.ParseException;
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
public class LoginController {

    @Autowired
    private SpotifyPlayerState sps;  

    @Autowired
    private InitializeAll initializer;
    
    // thread sleeps until awoken, and checks if token in db is valid, returns code 2xx if it is, if invalid, then sleeps again and repeats process
    @GetMapping("/login")
    public ResponseEntity login() {
        
        // setting headers to allow access from electron application from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        int responseCode;
        synchronized (LoginController.class) {
            try {
                while (Integer.toString(responseCode = HelperClass.verifyToken(DatabaseAccesser.getAccessToken())).charAt(0) != "2".charAt(0)) {
                    System.out.println("/login right before waiting");
                    sps.test.wait();
                    System.out.println("/login awoke from waiting");
                }
                
                if (sps.getConnected() == 0) {
                    
                    initializer.initialize();
                    System.out.println("initiated spotify player manager play status!");
                    sps.setConnected(1); 
                }
                System.out.println("/login before returning, code is: " + responseCode);
                return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
            }
            catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>("An error occured", headers, HttpStatus.ACCEPTED);
            } catch (InterruptedException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>("An error occured", headers, HttpStatus.ACCEPTED);
            }
        }
    }   
}
