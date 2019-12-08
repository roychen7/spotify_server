package com.spotify__server.controllers;

import java.io.BufferedReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spotify__server.components.SpotifyPlayerState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RestController
public class SpotifyStateUpdaterController {

    @Autowired
    private SpotifyPlayerState sps;
    
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
        return obj.getAsString("test");
        } catch (Exception e) {
            return "Failed";
        }
    }

    @ResponseBody @PutMapping("/update_play_status")
    public String updatePlayStatus(HttpServletResponse response, HttpServletRequest request) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.addHeader("Access-Control-Allow-Methods", "PUT");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");

        StringBuffer sb = new StringBuffer();
        String line = null;

        try {
            BufferedReader br = request.getReader();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String req_body_string = sb.toString();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(req_body_string);

            String req_body_value = obj.getAsString("value");

            boolean bool_val;

            if (req_body_value != null && !req_body_value.equals("")) {
                if ((bool_val = Boolean.parseBoolean(req_body_value)) == true || (bool_val == false)) {
                    sps.setPlayStatus(bool_val);
                    return "OK";
                }
            }

            return "Invalid body value";
        } catch (Exception e) {
            return "Exception was caught " + e.getMessage();
        }
    }

    @ResponseBody @PutMapping("/update_song_stats")
    public String updateSongStats(HttpServletResponse response, HttpServletRequest request) {
        
        
        return "";
    }
}