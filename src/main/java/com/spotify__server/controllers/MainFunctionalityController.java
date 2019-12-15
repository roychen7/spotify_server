package com.spotify__server.controllers;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spotify__server.components.managers.SpotifyPlayer;
import com.spotify__server.components.managers.SpotifyPlayerState;
import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.components.function_performers.AutoPlaylistAdder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.PlaylistGenerator;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.WeightedRandomGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;

// class for main functionality of the program, including generating playlist mode, retrieving recent 200 songs,
// and automatically adding to playlists

@RestController
public class MainFunctionalityController {

    @Autowired
    private SpotifyPlayer spotify_player;

    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private PlaylistDatabaseAccesser playlist_database_accesser;

    @Autowired
    private AutoPlaylistAdder auto_playlist_adder;
    
    @ResponseBody
    @GetMapping("/init_generator")
    public String initBiasedPlaylist(HttpServletResponse response) throws SQLException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");

        List<Song> random_playlist_song_list = playlist_database_accesser
                .getRandomPlaylistSongs(sps.getCompletedPlaylists());
        WeightedRandomGenerator wrg = new WeightedRandomGenerator(random_playlist_song_list);
        Queue<Song> play_queue = wrg.getBiasedTenSongs();

        sps.setPlayQueue(play_queue);

        PlaylistGenerator pg = new PlaylistGenerator(spotify_player, sps);
        pg.initPlaying();

        sps.setPlaylistGeneratorStatus(PlaylistGenStatus.TRUE);

        return "OK";
    }

    // TODO refactor make a playlist managing component, implement get 200 songs
    // method and move existing playlist properties to that class
    @RequestMapping(value = "/recent_200", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getRecent200Songs(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");

        return Collections.singletonMap("response", "hello!");
    }

    @RequestMapping(value = "/suggested_playlists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getSuggestedPlaylists(HttpServletResponse response, HttpServletRequest request)
            throws IOException, ParseException {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.addHeader("Access-Control-Allow-Methods", "PUT");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            StringBuffer sb = new StringBuffer();
            String line = "";
            BufferedReader br = request.getReader();
            
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String req_body_string = sb.toString();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(req_body_string);

            String in_song_id = obj.getAsString("value");
            List<String> results = auto_playlist_adder.getPlaylistRecommendations(in_song_id);
            
        } catch (IOException e) {
            System.out.println("caught IOException, " + e.toString());
        } catch (ParseException e) {
            System.out.println("caught parseException, " + e.toString());
        }
        return null;
    }
}