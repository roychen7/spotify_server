/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.initializers;

import com.spotify__server.components.accessers.spotify_api_access.GetInfoApi;
import com.spotify__server.components.accessers.spotify_api_access.InitModulesApi;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.List;
import javafx.util.Pair;
import net.minidev.json.parser.ParseException;

/**
 *
 * @author roychen
 */

public class PlaylistAndSongInitializer implements Runnable, Initializer {

    private GetInfoApi info_api_accesser; 
    
    private InitModulesApi init_modules_api_accesser;

    public PlaylistAndSongInitializer (GetInfoApi info_api_accesser, InitModulesApi init_modules_api_accesser) {
        this.info_api_accesser = info_api_accesser;
        this.init_modules_api_accesser = init_modules_api_accesser;
    }
    

    @Override
    public void run() {
        initialize();
    }

    @Override
    public void initialize() {
        System.out.println("PlaylistAndSongInitializer::initialize");
        try {
            String user_id = info_api_accesser.getUserId();
            List<Pair<String, String>> playlist_ids_and_names = init_modules_api_accesser.getAndUpdatePlaylistIdsAndNames(user_id);
            initPlaylistSongs(playlist_ids_and_names);  
        } catch (ClientProtocolException e) {
            System.out.println("Encountered ClientProtocolException error");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Encountered IOException error");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Encountered ParseException error");
            e.printStackTrace();
        }
    }

    public void initPlaylistSongs(List<Pair<String, String>> list_playlist_ids_names)
            throws ClientProtocolException, IOException, ParseException {
        for (int i = 0; i < list_playlist_ids_names.size(); i++) {
            System.out.println("INITIALIZING CURRENT PLAYLIST EQUALS = " + i);

            // pass playlist id into function from key-value pair
            // update songs, playlists, and artists database table data in one-pass as it is the quickest way
            init_modules_api_accesser.updateSongsPlaylistsArtists(list_playlist_ids_names.get(i).getKey(), 0);
        }
    }
}
