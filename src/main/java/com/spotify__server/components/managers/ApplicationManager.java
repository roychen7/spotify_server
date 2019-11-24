/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components.managers;

import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * @author roychen
 */

// class for managing application
public abstract class ApplicationManager {
    
    @Cacheable(cacheNames = "getToken")
    public String getAccessToken() throws SQLException, IOException {
        System.out.println("not cached!");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
            
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
    }
    
    @CachePut(cacheNames="getToken")
    public String updateAccessToken() throws SQLException, IOException {
        System.out.println("updating access token!");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
            
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
    }
}
