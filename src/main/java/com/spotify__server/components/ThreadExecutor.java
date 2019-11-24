/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
