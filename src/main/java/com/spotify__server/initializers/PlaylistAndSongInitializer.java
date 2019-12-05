/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.initializers;

import com.spotify__server.utils.SpotifyApiCaller;

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

    @Override
    public void run() {
        initialize();
    }

    @Override
    public void initialize() {
        System.out.println("PlaylistAndSongInitializer::getUserId");
        try {
            String user_id = SpotifyApiCaller.getUserId();
            List<Pair<String, String>> playlist_ids_and_names = SpotifyApiCaller.getAndUpdatePlaylistIdsAndNames(user_id);
            initPlaylistSongs(playlist_ids_and_names);  
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void initPlaylistSongs(List<Pair<String, String>> list_playlist_ids_names)
            throws ClientProtocolException, IOException, ParseException {
        for (int i = 0; i < list_playlist_ids_names.size(); i++) {
            System.out.println("INITIALIZING CURRENT PLAYLIST EQUALS = " + i);
            SpotifyApiCaller.updatePlaylistSongs(list_playlist_ids_names.get(i).getKey(), 0);
        }
    }
}
