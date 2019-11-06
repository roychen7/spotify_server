/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */
@RestController
public class TestDBController {
    
//    @RequestMapping("/hello") 
//    public String DBConnected() throws SQLException, IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Access-Control-Allow-Origin", "http://localhost:1234");
////        Connection con = JdbcRepository.getConnection();
//          if (con != null) {
//             return "DB CONNECTED";
//          } else {
//          return "DB NOt connected";
//          }
//    }
}
