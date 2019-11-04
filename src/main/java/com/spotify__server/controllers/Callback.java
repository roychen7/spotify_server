/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */
// 
@RestController
public class Callback {
    
    @RequestMapping("/callback")
    public ResponseEntity callback(@RequestParam String code) throws MalformedURLException, IOException, JSONException, ParseException {
        // initializing client and httpPost
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://accounts.spotify.com/api/token/");
        
        // adding headers for post request
        httpPost.setHeader("Authorization", "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
        
        // adding body for post request   
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/callback"));
        params.add(new BasicNameValuePair("scope", "user-modify-playback-state"));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = client.execute(httpPost);
          
        HttpEntity entity = response.getEntity();
        String jsonString = getResponseString(entity);
        System.out.println(jsonString);
          
        return new ResponseEntity<>(jsonString, HttpStatus.ACCEPTED); 
        }
    
    // code taken from https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java
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
    }

