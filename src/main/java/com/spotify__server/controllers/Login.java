/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spotify__server.repositories.JdbcRepository;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modules.VerifyToken;
import org.apache.http.HttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author roychen
 */
@RestController
public class Login {
    
    @GetMapping("/token")
    public ResponseEntity tokenExists() throws SQLException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        
        String str = "select * from `token` limit 1";
        ResultSet rs = stmt.executeQuery(str);
        
        if (rs.next()) {
            int code = VerifyToken.verifyToken(rs.getString(1));
            return new ResponseEntity<>(code, headers, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("400", headers, HttpStatus.ACCEPTED);
        }
    }   
    
    @GetMapping("/login")
    public ResponseEntity login() throws MalformedURLException, IOException, InterruptedException, SQLException {
//        URL obj = new URL("https://www.google.com");
//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequest
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
////        URL url = new URL("https://accounts.spotify.com/authorize?client_id=ba2aa172bb954f54be32398e8120381c&response_type=code&scope=user-modify-playback-state&redirect_uri=http://localhost:8080/callback");
////        HttpURLConnection con = (HttpURLConnection) url.openConnection();
////        con.setRequestMethod("GET");
//        HttpGet get = new HttpGet("https://accounts.spotify.com/authorize?client_id=ba2aa172bb954f54be32398e8120381c&response_type=code&scope=user-modify-playback-state&redirect_uri=http://localhost:8080/callback");
//        HttpClient client = HttpClients.createDefault();
//        
//        try {
//        HttpResponse response = client.execute(get);
//        HttpEntity entity =  response.getEntity();
//        return new ResponseEntity<String>(EntityUtils.toString(entity), headers, HttpStatus.ACCEPTED);
//        } catch (Error e) {
//            throw e;
//        }
//        return new ResponseEntity<>("Hello World updated!", headers, HttpStatus.ACCEPTED);
//          Thread.sleep(5000);
        Connection conn = JdbcRepository.getConnection();
        Statement stmt = conn.createStatement();
        String str = "select * from `token` limit 1";
        System.out.println("BEFORE LOOP");
        while (true) {
            System.out.println("INSIDE LOOP");
        ResultSet rs = stmt.executeQuery(str);
        String s = "";
        if (rs.next()) {
            System.out.println("Inside IF STATEMENT INSIDE LOOP ");
            s = rs.getString(1);
            System.out.println(s);
            int resp = VerifyToken.verifyToken(s);
            return new ResponseEntity<>(resp, headers, HttpStatus.ACCEPTED);
        }
        Thread.sleep(500);
        }
    }
}
