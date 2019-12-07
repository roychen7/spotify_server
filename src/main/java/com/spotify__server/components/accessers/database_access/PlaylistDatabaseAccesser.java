package com.spotify__server.components.accessers.database_access;

import org.springframework.stereotype.Component;

import com.spotify__server.modules.Song;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Component
public class PlaylistDatabaseAccesser extends DatabaseAccesser {
    
    public List<Song> getRandomPlaylistSongs(HashSet<String> completed_playlists) throws SQLException, IOException {
        List<String> playlist_ids = getPlaylistIds();
        Random random = new Random();
        String random_playlist_id = "";
        
        if (!completed_playlists.isEmpty()) {
        while (completed_playlists.contains(random_playlist_id = playlist_ids.get(random.nextInt(playlist_ids.size())))) {
            }
        } else {
            random_playlist_id = playlist_ids.get(random.nextInt(playlist_ids.size()));
        }
        
        completed_playlists.add(random_playlist_id);
        if (completed_playlists.size() == playlist_ids.size()) {
            completed_playlists.clear();
        }
        
        List<String> ret_string_format = getListFromDb(3, "select `song_uri`, `playcount`, `song_name` from `songs` where `playlist_id`='" + random_playlist_id +"'");
       
        List<Song> ret_songs = new ArrayList<>();
        
        for (int i = 0; i < ret_string_format.size(); i = i + 3) {
            Song s = new Song(ret_string_format.get(i), Integer.parseInt(ret_string_format.get(i + 1)), ret_string_format.get(i+2));
            ret_songs.add(s);
        }
        
        return ret_songs;
    }

    // below are more specific functions that can have cached results to improve performance
    @Cacheable(cacheNames="playlistNames")
    public List<String> getPlaylistIds() {
        return getListFromDb(1, "select `playlist_id` from `playlists`");
    }
    
    @CachePut(cacheNames="playlistNames")
    public List<String> updatePlaylistIds() {
        return getListFromDb(1, "select `playlist_id` from `playlists`");
    }
}