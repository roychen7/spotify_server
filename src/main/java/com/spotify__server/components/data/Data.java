package com.spotify__server.components.data;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

// "Data" class contains the ids of existing songs, playlists, and artists that are currently stored in the database
// used for minimizing time taken to refresh database data by preventing unnecessary queries through checking with set.contains()
@Component
public class Data {
    private Set<String> song_ids = new HashSet<>();
    private Set<String> playlist_ids = new HashSet<>();
    private Set<String> artist_ids = new HashSet<>();

    public void addToSongs(String id) {
        song_ids.add(id);
    }

    public void addToPlaylists(String id) {
        playlist_ids.add(id);
    }

    public void addToArtists(String id) {
        artist_ids.add(id);
    }

    public boolean containsSong(String id) {
        return song_ids.contains(id);
    }

    public boolean containsPlaylist(String id) {
        return playlist_ids.contains(id);
    }

    public boolean containsArtist(String id) {
        return artist_ids.contains(id);
    }
}