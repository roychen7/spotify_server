/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.utils.HelperClass;
import com.spotify__server.components.SpotifyPlayerState;
import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.initializers.InitializeAll;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

// Contains endpoints that deal with storing, retrieving, and getting access/refresh tokens
@RestController
@ComponentScan("com.spotify__server")
public class TokenController {
    static String access_token;
    static String refresh_token;
    
    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private InitializeAll initializer;
    
    // returns the token stored in the database
    @GetMapping("/token")
    public ResponseEntity getToken() {
        System.out.println("controllers:token/token");
        
        // allowing cross-origin access from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");

        String ress = DatabaseAccesser.getAccessToken();
         
        return new ResponseEntity<>(ress, headers, HttpStatus.ACCEPTED);
    }
        
    // test mapping to update SpotifyListener's cached access token
    @GetMapping("/update")
    public void test() throws IOException, SQLException {
        System.out.println("connecting from Token::/update");
        DatabaseAccesser.updateAccessToken();
    }
    
    
    // tests whether token is valid or not by calling HelperClass's verifyToken function 
    @GetMapping("/valid_token")
    public ResponseEntity tokenExists() {
        System.out.println("controllers:token:/valid_token");
        
        // allowing cross-origin access from localhost:3000
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            
            // grab the "access token" from the database
            String str = "select `access_token` from `token`";
            ResultSet rs = stmt.executeQuery(str);
            
            // set code to 2xx if access token is valid, default of 4xx (invalid)
            int code = 400;
            if (rs.next()) {
                code = HelperClass.verifyToken(rs.getString(1));
            }
            
            con.close();
            if (Integer.toString(code).charAt(0) == "2".charAt(0)) {
                if (sps.getConnected() == 0) {
                    
                    initializer.initialize();
                    System.out.println("initiated spotify player manager play status!");
                    sps.setConnected(1); 
                }
            System.out.println("SERVER connected: " + sps.getConnected());
            }
        
             return new ResponseEntity<>(code, headers, HttpStatus.ACCEPTED);
        } catch (SQLException ex) {
            System.out.println("sqlexception from token");
            return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (IOException ex) {
            System.out.println("ioException from token");
            return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.NOT_FOUND);
        }
    }
}
