package com.spotify__server.components.data;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

// "Data" class contains the ids of existing songs, playlists, and artists that are currently stored in the database
// used for minimizing time taken to refresh database data by preventing unnecessary queries through checking with set.contains()
@Component
public class Data {
    private Set<String> artist_ids = new HashSet<>();

    public void addToArtists(String id) {
        artist_ids.add(id);
    }

    public boolean containsArtist(String id) {
        return artist_ids.contains(id);
    }

    public Set<String> getArtists() {
        return artist_ids;
    }
}