package com.spotify__server.initializers;

import com.spotify__server.components.SpotifyPlayerState;

import org.springframework.stereotype.Component;

@Component
public class InitializeAll implements Initializer {
    private SpotifyPlayerState sps;
    private PauserInitializer pauser_initializer;
    private PlaylistAndSongInitializer playlist_and_song_initializer;
    private RefreshTokenInitializer refresh_token_initializer;

    public InitializeAll(SpotifyPlayerState sps) {
        this.sps = sps;
        pauser_initializer = new PauserInitializer(sps);
        playlist_and_song_initializer = new PlaylistAndSongInitializer();
        refresh_token_initializer = new RefreshTokenInitializer();
    }

    @Override
    public void initialize() {
        pauser_initializer.initialize();
        playlist_and_song_initializer.initialize();
        pauser_initializer.initialize();
        refresh_token_initializer.initialize();
        sps.initPlayStatus();
    }
}