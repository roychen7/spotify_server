/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components;

import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.executable.MainThread;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// Singleton for storing data related to database, and arbitrary elements such as the executor 
@Component
public class ThreadExecutor {
           
    // singular thread executor
    private Executor single_executor = Executors.newSingleThreadExecutor();
    
    public Executor getInstance() {
        return single_executor;
    }
  
}
