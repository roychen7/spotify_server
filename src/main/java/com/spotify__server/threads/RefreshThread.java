/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.threads;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author roychen
 */
public class RefreshThread implements Runnable {
    
    public void run() {
        HttpGet get = new HttpGet("http://localhost:8080/refresh");
        HttpClient client = HttpClients.createDefault();
        
        try {
            client.execute(get);
        } catch (IOException ex) {
            Logger.getLogger(RefreshThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
