/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spotify__server.repositories;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.stereotype.Repository;

/**
 *
 * @author roychen
 */
@Repository
public class JdbcRepository {
 
    // gets and returns a connection to mysql database according to properties in applocation.properties file 
    public static Connection getConnection() throws SQLException, FileNotFoundException, IOException {
        
        try {
            FileInputStream f = new FileInputStream("src/main/resources/application.properties");
            Properties p = new Properties();
            p.load(f);
            
            String url = p.getProperty("spring.datasource.url");
            String username = p.getProperty("spring.datasource.username");
            String password = p.getProperty("spring.datasource.password");

            return DriverManager.getConnection(url, username, password);
        } catch (IOException e) {
            throw e;
        }
    }
}