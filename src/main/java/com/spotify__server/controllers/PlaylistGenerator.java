/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.managers.SpotifyPlayerManager;
import com.spotify__server.database_access.DatabaseAccesser;
import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.WeightedRandomGenerator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

@RestController
public class PlaylistGenerator {
    private int i = 0;
    
    @Autowired
    private SpotifyPlayerManager spm;
    
    @GetMapping("/init_generator")
    public ResponseEntity initBiasedPlaylist() throws SQLException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        List<Song> random_playlist_song_list = DatabaseAccesser.getRandomPlaylistSongs(spm.getCompletedPlaylists());
        WeightedRandomGenerator wrg = new WeightedRandomGenerator(random_playlist_song_list);
        List<String> ret_val = wrg.getBiasedTenSongs();
        
        spm.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
        return new ResponseEntity(ret_val, headers, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/resume_generator")
    public ResponseEntity resumeBiasedPlaylists() {
        
        List<String> saved_list = spm.getSavedList();
        spm.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
        return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
    }
    
    @PutMapping("/save_current_generator_songs")
    public ResponseEntity saveCurrentGeneratorSongs() {
        
        
        spm.setPlaylistGeneratorStatus(PlaylistGenStatus.PAUSED);
        return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
    }
}
