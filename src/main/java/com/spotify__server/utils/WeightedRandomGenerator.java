/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.utils;

import com.spotify__server.modules.Song;
import static java.lang.Math.E;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author roychen
 */
public class WeightedRandomGenerator {

    public static List<Song> returnTen(List<Song> random_playlist_song_list) {
        
        return new ArrayList<>();
    }
    
    private NavigableMap<Integer, Song> map = new TreeMap<Integer, Song>();
    private Random random;
    private int total;
    
    public WeightedRandomGenerator(List<Song> song_list) {
        random = new Random();
        total = 0;
        for (int i = 0; i < song_list.size(); i++) {
            Song s = song_list.get(i);
            total += s.getWeight();
            map.put(total, s);
        }
    }
    
    public List<String> getBiasedTenSongs() {
        List<String> ret_string = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            ret_string.add(getNext());
        }
        return ret_string;
    }

    private String getNext() {
        int random_int = random.nextInt(total);
        Entry ret = map.higherEntry(random_int);
        Song song = (Song) ret.getValue();
        return song.getUri();
    }
}
