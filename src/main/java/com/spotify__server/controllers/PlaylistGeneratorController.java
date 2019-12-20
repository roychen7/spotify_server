// /*
//  * To change this license header, choose License Headers in Project Properties.
//  * To change this template file, choose Tools | Templates
//  * and open the template in the editor.
//  */
// package com.spotify__server.controllers;

// import com.spotify__server.components.managers.SpotifyPlayer;
// import com.spotify__server.components.managers.SpotifyPlayerState;
// import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
// import com.spotify__server.enums.PlaylistGenStatus;
// import com.spotify__server.modules.PlaylistGenerator;
// import com.spotify__server.modules.Song;
// import com.spotify__server.utils.WeightedRandomGenerator;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.sql.SQLException;
// import java.util.List;
// import java.util.Queue;

// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.bind.annotation.RestController;

// import net.minidev.json.JSONObject;
// import net.minidev.json.parser.JSONParser;
// import net.minidev.json.parser.ParseException;

// /**
//  *
//  * @author roychen
//  */

// @RestController
// public class PlaylistGeneratorController {
//     private int i = 0;
    
//     @Autowired
//     private SpotifyPlayer spotify_player;

//     @Autowired
//     private SpotifyPlayerState sps;

//     @Autowired
//     private PlaylistDatabaseAccesser playlist_database_accesser;
    
//     @ResponseBody @GetMapping("/init_generator")
//     public String initBiasedPlaylist(HttpServletResponse response) throws SQLException, IOException {

//         response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        
//         List<Song> random_playlist_song_list = playlist_database_accesser.getRandomPlaylistSongs(sps.getCompletedPlaylists());
//         WeightedRandomGenerator wrg = new WeightedRandomGenerator(random_playlist_song_list);
//         Queue<Song> play_queue = wrg.getBiasedTenSongs();
        
//         sps.setPlayQueue(play_queue);
        
//        PlaylistGenerator pg = new PlaylistGenerator(spotify_player, sps);
//        pg.initPlaying();
        
//         sps.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
        
//         return "OK";
//     }

//     @ResponseBody @PutMapping("/putPlaylistsArtists")
//     public String putPlaylistsArtists(HttpServletResponse response, HttpServletRequest request) {
//         System.out.println("controller::putPlaylistsArtists");
//         response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//         response.addHeader("Access-Control-Allow-Methods", "PUT");
//         response.addHeader("Access-Control-Allow-Headers", "Content-Type");

//         try {
//             StringBuffer sb = new StringBuffer();
//             String line = "";
//             BufferedReader br = request.getReader();
            
//             while ((line = br.readLine()) != null) {
//                 sb.append(line);
//             }

//             String req_body_string = sb.toString();
//             JSONParser parser = new JSONParser();
//             JSONObject obj = (JSONObject) parser.parse(req_body_string);

//             String req_body_value = obj.getAsString("value");
//             System.out.println("PRINTING VALUE: " + req_body_value);
//             int results = playlist_database_accesser.updateArtistsPlaylists(req_body_value);
//         } catch (IOException e) {
//             System.out.println("caught IOException, " + e.toString());
//         } catch (ParseException e) {
//             System.out.println("caught parseException, " + e.toString());
//         }
//         return null;
//     }

//     @ResponseBody @GetMapping("/getPlaylistsArtists")
//     public int getPlaylistsArtists(HttpServletResponse response, @RequestParam String value) {
//         System.out.println("getPlaylistsArtists controller called");
//         response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//         return playlist_database_accesser.getArtistPlaylists(value);
//     }
    
//     @GetMapping("/resume_generator")
//     public ResponseEntity resumeBiasedPlaylists() {
//         System.out.println("updatePlaylistsArtists function called");
        
//         sps.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);
//         return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
//     }
    
//     @PutMapping("/save_current_generator_songs")
//     public ResponseEntity saveCurrentGeneratorSongs() {
        
        
//         sps.setPlaylistGeneratorStatus(PlaylistGenStatus.PAUSED);
//         return new ResponseEntity(Integer.toString(i), HttpStatus.ACCEPTED);
//     }
// }
