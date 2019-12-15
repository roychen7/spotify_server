/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.modules;

/**
 *
 * @author roychen
 */
public class Playlist {
    private String playlist_name;
    private String playlist_id;
    private double average_song_playcount;

    public Playlist(String playlist_id) {
        this.playlist_id = playlist_id;
    }
}
