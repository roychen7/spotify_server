/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.utils;

import com.spotify__server.modules.Song;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author roychen
 */
public class WeightedRandomGenerator {
    // 4 + 10 + 5
    // 4   14  19
    
    private NavigableMap<Double, Song> map = new TreeMap<Double, Song>();
    private Random random;
    private double total;
    
    public WeightedRandomGenerator(List<Song> song_list) {
        
    }
}
