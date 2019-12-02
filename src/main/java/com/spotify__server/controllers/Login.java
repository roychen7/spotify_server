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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    // thread sleeps until awoken, and checks if token in db is valid, returns code 2xx if it is, if invalid, then sleeps again and repeats process
    @GetMapping("/login")
    public ResponseEntity login() {
        
        // setting headers to allow access from electron application from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        int responseCode;
        synchronized (spotify_player_manager.test) {
            try {
                while (Integer.toString(responseCode = HelperClass.verifyToken(DatabaseAccesser.getAccessToken())).charAt(0) != "2".charAt(0)) {
                    System.out.println("/login right before waiting");
                    spotify_player_manager.test.wait();
                    System.out.println("/login awoke from waiting");
                }
                
                MainThread t1 = new MainThread(spotify_player_manager);
                thread_executor.getInstance().execute(t1);
                
                spotify_player_manager.initPlayStatus();
                System.out.println("initiated spotify player manager play status!");
                spotify_player_manager.setConnected(1);
                
                Thread t2 = new Thread(new PlaylistInitializer(new SongInitializer()));
                t2.start();
                System.out.println("/login before returning, code is: " + responseCode);
                return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
            }
//        return new ResponseEntity<>(responseCode, headers, HttpStatus.ACCEPTED);
            catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>("An error occured", headers, HttpStatus.ACCEPTED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>("An error occured", headers, HttpStatus.ACCEPTED);
            } catch (SQLException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>("An error occured", headers, HttpStatus.ACCEPTED);
            } catch (ParseException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                return new ResponseEntity<>("An error occured", headers, HttpStatus.ACCEPTED);
            }
        }
    }   
}
