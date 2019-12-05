package com.spotify__server.initializers;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.spotify__server.components.SpotifyPlayer;
import com.spotify__server.components.SpotifyPlayerState;

import org.springframework.stereotype.Component;

@Component
public class InitializeAll implements Initializer {
    private SpotifyPlayerState sps;
    private SpotifyPlayer spotify_player;
    private PauserInitializer pauser_initializer;
    private PlaylistAndSongInitializer playlist_and_song_initializer;
    private RefreshTokenInitializer refresh_token_initializer;

    public void initInitializer (SpotifyPlayerState sps, SpotifyPlayer spotify_player) {
        this.sps = sps;
        this.spotify_player = spotify_player;
        // pauser_initializer = new PauserInitializer(sps, spotify_player);
        // playlist_and_song_initializer = new PlaylistAndSongInitializer();
        // refresh_token_initializer = new RefreshTokenInitializer();
    }

    @Override
    public void initialize() {
        sps.initPlayStatus();
        Executor executor = new ScheduledThreadPoolExecutor(3);
        executor.execute(new PauserInitializer(sps, spotify_player));
        executor.execute(new PlaylistAndSongInitializer());
        executor.execute(new RefreshTokenInitializer());
        // pauser_initializer.initialize();
        // playlist_and_song_initializer.initialize();
        // refresh_token_initializer.initialize();
    }
}