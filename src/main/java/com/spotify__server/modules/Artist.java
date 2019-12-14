package com.spotify__server.modules;

import java.util.PriorityQueue;

public class Artist {
    private String artist_id;
    private String artist_name;
    private PriorityQueue<Playlist> ordered_playlists;

    public Artist(String artist_id, String artist_name) {
        this.artist_id = artist_id;
        this.artist_name = artist_name;
    }

    public void setArtistId(String artist_id) {
        this.artist_id = artist_id;
    }
    
    public void setArtistName(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtistId() {
        return artist_id;
    }

    public String getArtistname() {
        return artist_name;
    }
}