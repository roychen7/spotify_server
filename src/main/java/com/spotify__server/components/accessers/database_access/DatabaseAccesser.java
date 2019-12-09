/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components.accessers.database_access;

import com.spotify__server.repositories.JdbcRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roychen
 */
@Component
@Primary
public class DatabaseAccesser {

    // below are generic functions that shouldn't have results cached
    public boolean insertIntoDb(String query) {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getSingleFromDb(String query) {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            } 
            return "NULL";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public List<String> getListFromDb(int no_columns, String query) {
        List<String> ret = new ArrayList<>();
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                for (int i = 1; i < no_columns + 1; i++) {
                    ret.add(rs.getString(i));
                }
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return ret;
        }
    }

    // below are more specific functions that can have cached results to improve performance
    @Cacheable(cacheNames = "getToken")
    public String getAccessToken() {
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
    public String updateAccessToken() {
        System.out.println("not cached!");

        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `access_token` from `token`");

            if (rs.next()) {
                return rs.getString(1);
            }
            return "";

        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
