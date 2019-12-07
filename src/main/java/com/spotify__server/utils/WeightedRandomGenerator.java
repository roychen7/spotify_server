/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.utils;

import com.spotify__server.modules.Song;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author roychen
 */
public class WeightedRandomGenerator {
    
    private NavigableMap<Integer, Song> map = new TreeMap<Integer, Song>();
    private Random random;
    private int total;
    
    public WeightedRandomGenerator(List<Song> song_list) {
        random = new Random();
        total = 0;
        for (int i = 0; i < song_list.size(); i++) {
            Song s = song_list.get(i);
            System.out.println("Song weight is: " + s.getWeight()); 
            total += s.getWeight();
            map.put(total, s);
        }
        System.out.println("TOTAL IS: " + total);
    }
    
    public Queue<Song> getBiasedTenSongs() {
        Queue<Song> ret_queue = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            Entry e = getNext();
            ret_queue.add( (Song) e.getValue() );
            map.remove( e.getKey() );
        }

        return ret_queue;
    }

    private Entry getNext() {
        int random_int = random.nextInt(total);
        Entry ret = map.higherEntry(random_int);
        return ret;
    }
}
