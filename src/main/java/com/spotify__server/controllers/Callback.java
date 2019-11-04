/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
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
@RestController
public class Callback {
    
//    @GetMapping("/callback")
//    @ResponseBody
    @RequestMapping("/callback")
    public String callback(@RequestParam String code) throws MalformedURLException, IOException, JSONException, ParseException {
        // initializing client and httpPost
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://www.example.com");
        
        // adding headers for post request
        httpPost.addHeader("Authorization", "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
        
        // adding body for post request
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("redirect_uri", "http://localhost:8080/callback");
        json.put("grant_type", "authorization_code");
        json.put("scope", "user-modify-playback-state");
//        
//        // executing post request
          String s = json.toString();
          String format = s.replaceAll("\\\\", "");
          StringEntity se = new StringEntity(format, "UTF-8");
          httpPost.setEntity(se);
          HttpResponse response = client.execute(httpPost);
          
          HttpEntity entity = response.getEntity();
          String jsonString = EntityUtils.toString(entity);
          JSONObject object = new JSONObject(jsonString);
          
          return object.getString("access_token");
    
          
//        return new ResponseEntity<>("HI", HttpStatus.ACCEPTED);
//          return response
        }
    }
