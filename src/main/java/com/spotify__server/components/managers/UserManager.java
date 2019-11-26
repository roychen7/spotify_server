/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components.managers;

import com.spotify__server.repositories.JdbcRepository;
import com.spotify__server.database_access.DatabaseAccesser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 *
 * @author roychen
 */

// get all of a user's playlists in a list, can access each of the playlists' respective names and ids
// in the database, store playlist by id

// deals with managing user information (eg. access token, user id, user playlists, etc.)
@Component
public class UserManager {
    
    public void updateProperties() {
        try {
            String s = getUserId();
            HttpGet get = new HttpGet("https://api.spotify.com/v1/users/" + s + "/playlists");
            get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
            HttpClient client = HttpClients.createDefault();
            
            HttpResponse http_response = client.execute(get);
            String response_string = EntityUtils.toString(http_response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject json_response_object = (JSONObject) parser.parse(response_string);
            
            System.out.println(json_response_object.get("items").getClass());
            JSONArray resp_array = (JSONArray) json_response_object.get("items");
            
            List<Pair<String, String>> playlist_ids_list = new ArrayList<>();
            
            for (int i = 0; i < resp_array.size(); i++) {
                JSONObject obj = (JSONObject) resp_array.get(i);
                playlist_ids_list.add(new Pair(obj.getAsString("id"), obj.getAsString("name")));
            }
            
            initUserPlaylistAndSongsInDb(playlist_ids_list);            
            
        } catch (SQLException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initUserPlaylistAndSongsInDb(List<Pair<String, String>> playlist_ids_list) throws SQLException, IOException {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery("select playlist_id from playlists");
            HashSet<String> playlist_ids_from_db = new HashSet<>();
            
            while (rs.next()) {
                playlist_ids_from_db.add(rs.getString(1));
            }
            
            List<Pair<String, String>> tables_to_add_playlist = missingPlaylists(playlist_ids_from_db, playlist_ids_list);
            List<String> list_playlist_ids = new ArrayList<>();
            
            for (int i = 0; i < tables_to_add_playlist.size(); i++) {
                stmt.executeUpdate("insert into `playlists` (`playlist_id`, `playlist_name`) values "
                    + "('" + tables_to_add_playlist.get(i).getKey() + "', '"+ tables_to_add_playlist.get(i).getValue() + "');");
                list_playlist_ids.add(tables_to_add_playlist.get(i).getKey());
            }
            con.close();
//            initPlaylistSongs(list_playlist_ids);
        }
    }
    
    private void initPlaylistSongs(List<String> list_playlist_ids) throws SQLException, IOException, ParseException {
        for (int i = 0; i < list_playlist_ids.size(); i++) {
            initSongsFromPlaylistId(list_playlist_ids.get(i));
        }
    }

    private void initSongsFromPlaylistId(String playlist_id) throws SQLException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/playlists/" + playlist_id + "/tracks?fields=items(added_at%2Ctrack(name%2Cid%2Cduration_ms%2Calbum%2Cartists))");
        HttpClient client = HttpClients.createDefault();
        
        get.addHeader("Authorization", "Bearer " + DatabaseAccesser.getAccessToken());
        
        HttpResponse response = client.execute(get);
        String response_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response_string);
    }
    
    private List<Pair<String, String>> missingPlaylists(HashSet<String> db_playlists, List<Pair<String, String>> user_playlists) {
        List<Pair<String, String>> ret = new ArrayList<>();
        
        for (int i = 0; i < user_playlists.size(); i++) {
            if (!db_playlists.contains(user_playlists.get(i).getKey())) {
                ret.add(new Pair(user_playlists.get(i).getKey(), user_playlists.get(i).getValue()));
            }
        }
        
        return ret;
    }
    
    public String getUserId() throws SQLException, IOException, ParseException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me");
        HttpClient client = HttpClients.createDefault();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        JSONParser parser = new JSONParser();
        
        String access_token = DatabaseAccesser.getAccessToken();
        get.addHeader("Authorization", "Bearer " + access_token);
        HttpResponse resp = client.execute(get);
        HttpEntity entity = resp.getEntity();
        String s = EntityUtils.toString(entity);
        JSONObject jsonObj = (JSONObject) parser.parse(s);
        
        return jsonObj.getAsString("id");
    }
}
