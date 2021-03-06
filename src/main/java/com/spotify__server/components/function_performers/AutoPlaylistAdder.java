package com.spotify__server.components.function_performers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.components.accessers.spotify_api_access.GetInfoApi;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;
import com.spotify__server.modules.Artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.minidev.json.parser.ParseException;

@Component
public class AutoPlaylistAdder {

    @Autowired
    private GetInfoApi info_api_accesser;

    @Autowired
    private PlaylistDatabaseAccesser playlist_database_accesser;

    // returns playlist recommendations for given song id (by grabbing the artists)
    public List<String> getPlaylistRecommendations(String song_id) {
        try {
            List<Artist> songArtists = info_api_accesser.getSongArtists(song_id);
            return getPlaylistsForArtists(songArtists);

        } catch (IOException | ParseException e) {
            System.out.println("caught an exception!");
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getPlaylistsForArtists(List<Artist> songArtists) {
        Set<String> return_set = new LinkedHashSet<>();

        for (int i = 0; i < songArtists.size(); i++) {
            return_set.addAll(playlist_database_accesser.getAssociatedPlaylists(songArtists.get(i).getArtistId()));
        }

        return new ArrayList<>(return_set);
    }
}