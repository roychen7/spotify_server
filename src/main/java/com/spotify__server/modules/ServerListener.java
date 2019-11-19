/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

import java.util.Observable;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */
@Component
public class ServerListener {
    private int connected;
    
    public void setConnected(int a) {
        connected = a;
    }
    
    public int getConnected() {
        return connected;
    }
    
    
}
