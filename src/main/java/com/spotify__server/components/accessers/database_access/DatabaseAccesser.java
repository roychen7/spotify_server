/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.components.accessers.database_access;

import com.spotify__server.modules.Song;
import com.spotify__server.repositories.JdbcRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

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

/**
 *
 * @author roychen
 */
@Component
public class DatabaseAccesser {

    // below are generic functions that shouldn't have results cached
    public boolean insertIntoDb(String query) {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getSingleFromDb(String query) {
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            } 
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public List<String> getListFromDb(int no_columns, String query) {
        List<String> ret = new ArrayList<>();
        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                for (int i = 1; i < no_columns + 1; i++) {
                    ret.add(rs.getString(i));
                }
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return ret;
        }
    }
    
    @Cacheable(cacheNames="playlistNames")
    public List<String> getPlaylistIds() {
        return getListFromDb(1, "select `playlist_id` from `playlists`");
    }
    
    @CachePut(cacheNames="playlistNames")
    public List<String> updatePlaylistIds() {
        return getListFromDb(1, "select `playlist_id` from `playlists`");
    }

    // below are generic functions that don't need to be cached
    public List<Song> getRandomPlaylistSongs(HashSet<String> completed_playlists) throws SQLException, IOException {
        List<String> playlist_ids = getPlaylistIds();
        Random random = new Random();
        String random_playlist_id = "";
        
        if (!completed_playlists.isEmpty()) {
        while (completed_playlists.contains(random_playlist_id = playlist_ids.get(random.nextInt(playlist_ids.size())))) {
            }
        } else {
            random_playlist_id = playlist_ids.get(random.nextInt(playlist_ids.size()));
        }
        
        completed_playlists.add(random_playlist_id);
        if (completed_playlists.size() == playlist_ids.size()) {
            completed_playlists.clear();
        }
        
        List<String> ret_string_format = getListFromDb(3, "select `song_uri`, `playcount`, `song_name` from `songs` where `playlist_id`='" + random_playlist_id +"'");
       
        List<Song> ret_songs = new ArrayList<>();
        
        for (int i = 0; i < ret_string_format.size(); i = i + 3) {
            Song s = new Song(ret_string_format.get(i), Integer.parseInt(ret_string_format.get(i + 1)), ret_string_format.get(i+2));
            ret_songs.add(s);
        }
        
        return ret_songs;
    }


    @Cacheable(cacheNames = "getToken")
    public String getAccessToken() {
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
    public String updateAccessToken() {
        System.out.println("not cached!");

        try (Connection con = JdbcRepository.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select `access_token` from `token`");

            if (rs.next()) {
                return rs.getString(1);
            }
            return "";

        } catch (IOException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccesser.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
