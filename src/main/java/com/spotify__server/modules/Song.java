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
public class Song {
    private String playlist_id;
    private String playlist_name;
    private String name;
    private String uri;
    private int duration_ms;
    private int playcount;
    private int weight;
    
    public Song(String uri, int playcount, String song_name,String playlist_name) {
        this.uri = uri;
        this.playcount = playcount;
        this.playlist_name = playlist_name;
        
        calculateWeight(playcount);
    }
    
    private void calculateWeight(int playcount) {
       if (playcount >= 0 && playcount <= 2) {
           weight = 1;
       } else if (playcount >= 3 && playcount <= 5) {
           weight = 2;
       } else if (playcount >= 6) {
           weight = 3;
       }
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public int getDuration() {
        return duration_ms;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPlaylistName() {
        return playlist_name;
    }
}
