package com.spotify__server.components.function_performers;

import java.io.IOException;
import java.util.List;

import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.spotify_api_access.GetInfoApi;
import com.spotify__server.modules.Song;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SongUpdater {

    @Autowired
    private DatabaseAccesser database_accesser;

    @Autowired
    private GetInfoApi info_api_accesser;

    public void updateSongStats(String song_id) {
        // 1. update song playcount
        // 2. update song last played date
        // if song entry exists in the database, update its playcount and last played
        // date, else insert song into the database table `songs`

        Song song;
        try {
            song = info_api_accesser.getSongDetails(song_id);
            String print;
            database_accesser.updateIntoDb((print = "insert into `songs` values('" + song_id + "', '" + song.getUri() + "', '" + song.getName() + "', '" + 
            Integer.toString(song.getDuration()) + "', '0', current_timestamp()) on duplicate key update `playcount`='" + 
            incPlayCount(database_accesser.getSingleFromDb("select `playcount` from `songs` where `song_uri`='" + song.getUri() + "'")) + "', `last_played`=current_timestamp()"));
            
            System.out.println("PRINT IS: " + print);
            removeSongIfBelowTop200();
        } catch (ParseException | IOException | net.minidev.json.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    // will grab top however many songs that have a last_played value 
    // in db ordered by date in descending order, remove all the songs that are not in the top 
    // 200 recently played, AND also not 
    // in the user's playlist (eg. playlist_id == NULL)
    private void removeSongIfBelowTop200() {
        List<String> res = database_accesser.getListFromDb(2, "select `song_uri`, `playlist_id` from `songs` where `last_played` is not null order by `last_played` desc");

        int diff = 0;
        String visible = "";

        if ((diff = res.size() - 400) > 0) {
            for (int i = 0; i < diff; i = i + 2) {
                if ((visible = res.get(i+1)).equals("NULL")) {
                    System.out.println(visible);
                    String song_uri = res.get(i);
                    database_accesser.updateIntoDb("delete from `songs` where `song_uri`='" + song_uri + "'");
                }
            }
        }
    }

    // inc play count helper for current song in removeSongIfBelowTop200() 
    private String incPlayCount(String playCount) {
        return Integer.toString(Integer.parseInt(playCount) + 1);
    }
}