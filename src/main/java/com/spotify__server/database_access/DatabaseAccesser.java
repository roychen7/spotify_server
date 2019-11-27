/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.database_access;

import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * @author roychen
 */
public class DatabaseAccesser {
    
    @Cacheable(cacheNames = "getToken")
    public static String getAccessToken()  {
        System.out.println("not cached!");
            
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        
        if (rs.next()) {
            return rs.getString(1);
        }
        
        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    @CachePut(cacheNames = "getToken")
    public static String updateAccessToken()  {
        System.out.println("not cached!");
            
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        
        if (rs.next()) {
            return rs.getString(1);
        }
        
        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static void insertIntoDb(String query) throws SQLException, IOException {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
    }
    
    public static HashSet<String> getExistingSongs() throws SQLException, IOException {
        System.out.println("DAtabaseAccesser::getExistingSongs");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `song_id` from `songs`");
            HashSet<String> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        }
    }
}
