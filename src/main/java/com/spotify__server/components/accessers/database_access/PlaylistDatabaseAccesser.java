package com.spotify__server.components.accessers.database_access;

import org.springframework.stereotype.Component;

import com.spotify__server.modules.Playlist;
import com.spotify__server.modules.Song;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

@Component
public class PlaylistDatabaseAccesser extends DatabaseAccesser {
    
    // grabs all the songs associated with the randomly selected playlist from the user's playlist db table
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

    // this method will never execute and will instead return the cached result stored by the eager initialization of updateAssociatedPlaylists()
    @Cacheable(value="getAssociatedPlaylists", key = "'#p0'")
    public List<String> getAssociatedPlaylists(String artistId) {
        return null;
    }

    // returns playlists associated with the artistId in order from highest to lowest number of appearances
    // in said playlist, and only those whose appearance count are >= the constraint 
    // (set by the top playlist appearance count divided by 2). This is to grab the most relevant recommendations
    // for playlists that the user owns, that are associated with the artist
    @CachePut(value="getAssociatedPlaylists", key = "'#p0'")
    public List<Playlist> updateAssociatedPlaylists(String artistId) {
        List<String> results = getListFromDb(1, "select songsToPlaylists.playlist_id from songsToArtists inner join " +
        "songsToPlaylists on songsToPlaylists.song_id=songsToArtists.song_id where songsToArtists.artist_id='" + artistId + "' order by playlist_id");
        PriorityQueue<ArtistPlaylistAppearanceCount> max_playlist_pq = new PriorityQueue(1, new comparePlaylists());
        
        String current_playlist_id = "";
        int current_playlist_count = 0;
        for (int i = 0; i < results.size(); i++) {
            if (current_playlist_id.equals("")) {
                current_playlist_id = results.get(i);
                current_playlist_count++;
                continue;
            } else {
                if (!results.get(i).equals(current_playlist_id)) {
                    ArtistPlaylistAppearanceCount apac = new ArtistPlaylistAppearanceCount(current_playlist_count, new Playlist(current_playlist_id));
                    max_playlist_pq.add(apac);
                    current_playlist_id = results.get(i);
                    current_playlist_count = 1;
                } else {
                    current_playlist_count++;
                }
            }
        }

        return getQualifyingPlaylistsFromPQ(max_playlist_pq);
    }

        
    // returns list of playlists in the max priority queue that satisfy constraint, returns once 
    // we dip below the constraint
    private List<Playlist> getQualifyingPlaylistsFromPQ(PriorityQueue<ArtistPlaylistAppearanceCount> max_playlist_pq) {
        List<Playlist> ret = new ArrayList<>();
        if (max_playlist_pq.isEmpty()) {
            return ret;
        }

        ArtistPlaylistAppearanceCount apac = max_playlist_pq.poll();
        int constraint = apac.getArtistCount() / 2;

        ArtistPlaylistAppearanceCount temp_apac;
        
        while (!max_playlist_pq.isEmpty() && ((temp_apac = max_playlist_pq.poll()).getArtistCount() >= constraint)) {
            ret.add(temp_apac.getPlaylist());
        }
        
        return ret;
    }

    // class for comparing playlists by pairing together artist appearance count, along with the 
    // respective playlist
    static class ArtistPlaylistAppearanceCount {
        private int artist_count;
        private Playlist playlist;

        public ArtistPlaylistAppearanceCount(int artist_count, Playlist playlist) {
            this.artist_count = artist_count;
            this.playlist = playlist;
        }

        public int getArtistCount() {
            return artist_count;
        }

        public Playlist getPlaylist() {
            return playlist;
        }
    }
    
    // function to use as comparator for priority queue
    static class comparePlaylists implements Comparator <ArtistPlaylistAppearanceCount> {

        @Override
        public int compare(ArtistPlaylistAppearanceCount o1, ArtistPlaylistAppearanceCount o2) {
            if (o1.getArtistCount() < o2.getArtistCount()) {
                return 1;
            } else if (o1.getArtistCount() == o2.getArtistCount()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}