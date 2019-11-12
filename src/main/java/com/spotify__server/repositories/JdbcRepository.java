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
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

/**
 *
 * @author roychen
 */
@Repository
public class JdbcRepository {
 
    public static Connection getConnection() throws SQLException, FileNotFoundException, IOException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        try {
            FileInputStream f = new FileInputStream("src/main/resources/application.properties");
            Properties p = new Properties();
            p.load(f);
            
            String url = p.getProperty("spring.datasource.url");
            String username = p.getProperty("spring.datasource.username");
            String password = p.getProperty("spring.datasource.password");
            
//            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//            dataSource.setUrl(url);
//            dataSource.setUsername(username);
//            dataSource.setPassword(password);
            
            return DriverManager.getConnection(url, username, password);
        } catch (IOException e) {
            throw e;
        }
//        return dataSource;
    }
}