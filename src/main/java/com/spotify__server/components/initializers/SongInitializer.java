package com.spotify__server.components.initializers;

import java.io.IOException;
import java.sql.SQLException;

import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.repositories.JdbcRepository;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minidev.json.JSONArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.lang.StringBuilder;

/**
 *
 * @author roychen
 */

 public class SongInitializer {
     
    public void initPlaylistSongs(List<String> list_playlist_ids) throws SQLException, IOException, ParseException, net.minidev.json.parser.ParseException {
//        System.out.println("SongInitializer::initPlaylistSongs");
        for (int i = 0; i < list_playlist_ids.size(); i++) {
            System.out.println("INITIALIZING CURRENT PLAYLIST EQUALS = " + i);
            initSongsFromPlaylistId(list_playlist_ids.get(i));
        }
    }
     
    private void initSongsFromPlaylistId(String playlist_id) throws SQLException, IOException, ParseException, net.minidev.json.parser.ParseException {
//        System.out.println("SongInitializer::initSongsFromPlaylistId");
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlist_id + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists))");
        HttpClient client = HttpClients.createDefault();
        
        get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
        
        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);
        JSONArray items_array = (JSONArray) obj.get("items");
        
        HashSet<String> existing_song_ids = DatabaseAccesser.getExistingSongs();
        
        for (int i = 0; i < items_array.size(); i++) {
            JSONObject ith_track = (JSONObject) items_array.get(i);
            JSONObject track_details = (JSONObject) ith_track.get("track");
            String song_name = track_details.getAsString("name");
            String song_id = track_details.getAsString("id");
            String song_duration = track_details.getAsString("duration_ms");
            
            String insert_string = "";
            
            // index of any quotation marks (eg. ') in song name. If there exists, must handle
            int ind_of_quote = -1;
            
                if (!existing_song_ids.contains(song_id)) {
                    if ((ind_of_quote = song_name.indexOf("'")) == -1) {
                    insert_string = "insert into `songs` values ('" + song_id + "', '" + song_name + "', '" + song_duration + "')";
                } else {
                    String song_name_reformatted = handleQuotation(song_name, ind_of_quote);   
                    insert_string = "insert into `songs` values ('" + song_id + "', '" + song_name_reformatted + "', '" + song_duration + "')";
                }
            }
            if (!insert_string.equals("")) {
            DatabaseAccesser.insertIntoDb(insert_string);   
            }
        }
    }
    
    private String handleQuotation(String song_name, int ind_of_quote) {  
        StringBuilder ret = new StringBuilder(song_name.substring(0, ind_of_quote));
        
        for (int i = ind_of_quote; i < song_name.length(); i++) {
            if (song_name.charAt(i) == "'".charAt(0)) {
                ret.append("''");
                continue;
            }
            ret.append(song_name.charAt(i));
        }
        return ret.toString();
    }
 }


// It's A Flex
// ind_of_quote = 2
// return "It" + 
// "'It" + "'" + "s A Flex"