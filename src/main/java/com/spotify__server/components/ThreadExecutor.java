package com.spotify__server.components;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

@Component
public class ThreadExecutor {
           
    // singular thread executor
    private Executor single_executor = Executors.newSingleThreadExecutor();
    
    public Executor getInstance() {
        return single_executor;
    }
  
}
