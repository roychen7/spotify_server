/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author roychen
 */

// a utility class for arbitrary helper functions
public class HelperClass {
    
    // code taken from https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java
    // This function converts httpentity into string format
    public static String getResponseString(HttpEntity entity) {
    StringBuilder builder = new StringBuilder();
    if (entity != null) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException e) {
            return null;
        }
    }
    
    return builder.toString();
    }
    
    // param str is access token, ánd the code checks the access token against a certain spotify api call,
    // for the sole purpose of determining if the given access token is valid or not. Return 
    // value is response code of api call, 2xx if valid token and 4xx if invalid token

    // TODO: refactor to return true or false instead of int code
    public static int verifyToken(String str) throws MalformedURLException, IOException {  
        if (str.equals("")) {
            return 4;
        }      
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.setHeader("Authorization", "Bearer " + str);
        int code = 0;
        
        try {
            HttpResponse response = client.execute(get);
            code = response.getStatusLine().getStatusCode();
           System.out.println("THE CODE IS " +Integer.toString(code));
        } catch (Error e) {
            throw e;
        }
        return code;
    }
}
