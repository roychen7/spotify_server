/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.SpotifyPlayer;
import com.spotify__server.components.SpotifyPlayerState;
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.PlaylistGenerator;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.WeightedRandomGenerator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;

import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

@RestController
public class PlaylistGeneratorController {
    private int i = 0;
    
    @Autowired
    private SpotifyPlayer spotify_player;

    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private PlaylistDatabaseAccesser playlist_database_accesser;
    
    @ResponseBody @GetMapping("/init_generator")
    public String initBiasedPlaylist(HttpServletResponse response) throws SQLException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        
        List<Song> random_playlist_song_list = playlist_database_accesser.getRandomPlaylistSongs(sps.getCompletedPlaylists());
        WeightedRandomGenerator wrg = new WeightedRandomGenerator(random_playlist_song_list);
        Queue<Song> play_queue = wrg.getBiasedTenSongs();
        
        sps.setPlayQueue(play_queue);
        
       PlaylistGenerator pg = new PlaylistGenerator(spotify_player, sps);
       pg.initPlaying();
        
        sps.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
        
        return "OK";
    }
    
    @GetMapping("/resume_generator")
    public ResponseEntity resumeBiasedPlaylists() {
        
        sps.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
        return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
    }
    
    @PutMapping("/save_current_generator_songs")
    public ResponseEntity saveCurrentGeneratorSongs() {
        
        
        sps.setPlaylistGeneratorStatus(PlaylistGenStatus.PAUSED);
        return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
    }
}
