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
        
        con.close();
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
        
        con.close();
        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
