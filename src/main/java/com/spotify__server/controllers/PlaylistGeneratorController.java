/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.SpotifyPlayerState;
import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.WeightedRandomGenerator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.util.Pair;
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
public class PlaylistGeneratorController {
    private int i = 0;
    
    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private DatabaseAccesser database_accesser;
    
    @GetMapping("/init_generator")
    public ResponseEntity initBiasedPlaylist() throws SQLException, IOException {
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        
        List<Song> random_playlist_song_list = database_accesser.getRandomPlaylistSongs(sps.getCompletedPlaylists());
        WeightedRandomGenerator wrg = new WeightedRandomGenerator(random_playlist_song_list);
        Queue<Song> play_queue = wrg.getBiasedTenSongs();
        
        sps.setPlayQueue(play_queue);
//        
//        PlaylistGenerator pg = new PlaylistGenerator(sps);
//        pg.initPlaying();
        
        
        sps.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
        
        List<String> ret_temp = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Song s = play_queue.poll();
            ret_temp.add(s.getName());
        }
        
        return new ResponseEntity(ret_temp, headers, HttpStatus.ACCEPTED);
        
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
