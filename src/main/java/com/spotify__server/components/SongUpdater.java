package com.spotify__server.components;

import java.io.IOException;

import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;
import com.spotify__server.modules.Song;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SongUpdater {

    @Autowired
    private DatabaseAccesser database_accesser;

    @Autowired
    private SpotifyApiAccesser api_accesser;

    public void updateSongStats(String song_id) {
        // 1. update song playcount
        // 2. update song last played date
        // if song entry exists in the database, update its playcount and last played
        // date
        // else insert new song
        // 3. remove song from `songs` table if song doesn't exist in any of user's
        // playlists AND there are 200 or more songs that
        // have been played before the given song (regardless if these songs were in the
        // user's playlists or not)

        Song song;
        try {
            song = api_accesser.getSongDetails(song_id);
            database_accesser.insertIntoDb("insert into table values('" + song.getPlaylistId() + "', '" + song.getUri() + "', '" + song.getName() + "', '" + 
            Integer.toString(song.getDuration()) + "', '0', current_timestamp()) on duplicate key update `playcount`='" + 
            incPlayCount(database_accesser.getSingleFromDb("select `playcount` from `songs` where `song_uri`='" + song.getUri() + "'")) + "', `last_played`=current_timestamp()");
        } catch (ParseException | IOException | net.minidev.json.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private String incPlayCount(String playCount) {
        return Integer.toString(Integer.parseInt(playCount) + 1);
    }
}