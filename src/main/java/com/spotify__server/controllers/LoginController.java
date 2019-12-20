/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.initializers.InitializeAll;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import com.spotify__server.utils.HelperClass;
import com.spotify__server.components.managers.SpotifyPlayer;
import com.spotify__server.components.managers.SpotifyPlayerState;
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private SpotifyApiAccesser api_accesser;

    @Autowired
    private SpotifyPlayer spotify_player;

    @Autowired
    private InitializeAll initializer;

    @Autowired
    private DatabaseAccesser database_accesser;
    
    // thread sleeps until awoken, and checks if token in db is valid, returns code 2xx if it is, if invalid, then sleeps again and repeats process
    @ResponseBody @GetMapping("/login")
    public String login(HttpServletResponse response) {
        
        // setting headers to allow access from electron application from localhost:3000
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        
        int responseCode;
        synchronized (LoginController.class) {
            try {
                while (Integer.toString(responseCode = HelperClass.verifyToken(database_accesser.getAccessToken())).charAt(0) != "2".charAt(0)) {
                    System.out.println(database_accesser.getAccessToken());
                    System.out.println("/login right before waiting");
                    LoginController.class.wait();
                    System.out.println("/login awoke from waiting");
                }
                
                if (sps.getConnected() == 0) {
                    initializer.initInitializer(api_accesser, sps, spotify_player);
                    initializer.initialize();
                    System.out.println("initiated spotify player manager play status!");
                    sps.setConnected(1); 
                }
                System.out.println("/login before returning, code is: " + responseCode);
                return "OK";
            }
            catch (IOException ex) {
                return "An error occured " + ex.getMessage();
            } catch (InterruptedException ex) {
                return "An error occured " + ex.getMessage();
            }
        }
    }   
}
