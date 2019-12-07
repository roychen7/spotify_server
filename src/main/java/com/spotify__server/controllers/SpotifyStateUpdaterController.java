package com.spotify__server.controllers;

import java.io.BufferedReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RestController
public class SpotifyStateUpdaterController {
    
    @ResponseBody @PutMapping("/testputmapping")
    public String testPutMapping(final HttpServletResponse response, final HttpServletRequest request) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.addHeader("Access-Control-Allow-Methods", "PUT");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");

        final StringBuffer jb = new StringBuffer();
        String line = null;

        try {
            final BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        System.out.println(jb);
        String jbString = jb.toString();

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(jbString);
        System.out.println(obj.getAsString("test"));

        } catch (final Exception e) {

        }
                
        return "";
    }
}