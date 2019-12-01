/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.database_access;

import com.spotify__server.modules.Song;
import com.spotify__server.repositories.JdbcRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * @author roychen
 */
public class DatabaseAccesser {
    
    @Cacheable(cacheNames = "getToken")
    public static String getAccessToken()  {
        System.out.println("not cached!");
            
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        
        if (rs.next()) {
            return rs.getString(1);
        }
        
        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    @CachePut(cacheNames = "getToken")
    public static String updateAccessToken()  {
        System.out.println("not cached!");
            
        try (Connection con = JdbcRepository.getConnection()) {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select `access_token` from `token`");
        
        if (rs.next()) {
            return rs.getString(1);
        }
        
        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static void insertIntoDb(String query) throws SQLException, IOException {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
    }
    
    public static String getSingleFromDb(String query) throws SQLException, IOException {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            } 
            return "";
        }
    }
    
    public static List<String> getListFromDb(int no_columns, String query) throws SQLException, IOException {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            List<String> ret = new ArrayList<>();
            while (rs.next()) {
                for (int i = 1; i < no_columns + 1; i++) {
                    ret.add(rs.getString(i));
                }
            }
            return ret;
        }
    }
    
    @Cacheable(cacheNames="getExistingSongs")
    public static HashSet<String> getExistingSongs() throws SQLException, IOException {
        System.out.println("DAtabaseAccesser::getExistingSongs");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `song_id` from `songs`");
            HashSet<String> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        }
    }
    
    @Cacheable(cacheNames="getExistingSongs")
    public static HashSet<String> updateExistingSongs() throws SQLException, IOException {
        System.out.println("DAtabaseAccesser::updateExistingSongs");
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `song_id` from `songs`");
            HashSet<String> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        }
    }
    
    @Cacheable(cacheNames="playlistNames")
    public static List<String> getPlaylistNames() throws SQLException, IOException {
        return getListFromDb(1, "select `playlist_name` from `playlists`");
    }
    
    @CachePut(cacheNames="playlistNames")
    public static List<String> updatePlaylistNames() throws SQLException, IOException {
        return getListFromDb(1, "select `playlist_name` from `playlists`");
    }

    public static List<Song> getRandomPlaylistSongs(HashSet<String> completed_playlists) throws SQLException, IOException {
        List<String> playlist_names = getPlaylistNames();
        Random random = new Random();
        String random_playlist_name;
        
        while (!completed_playlists.contains(random_playlist_name = playlist_names.get(random.nextInt(playlist_names.size())))) {
        }
        
        completed_playlists.add(random_playlist_name);
        if (completed_playlists.size() == playlist_names.size()) {
            completed_playlists.clear();
        }
        
        List<String> ret_string_format = getListFromDb(2, "select `song_uri`, `playcount` from `songs` where `playlist_name`='" + random_playlist_name +"'");
       
        List<Song> ret_songs = new ArrayList<>();
        
        for (int i = 0; i < ret_string_format.size(); i = i + 2) {
            Song s = new Song(ret_string_format.get(i), Integer.parseInt(ret_string_format.get(i + 1)));
            ret_songs.add(s);
        }
        
        return ret_songs;
    }
    
    
}
