package com.spotify__server.initializers;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.spotify__server.components.SpotifyPlayer;
import com.spotify__server.components.SpotifyPlayerState;
import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitializeAll implements Initializer {
    @Autowired
    private SpotifyPlayerState sps;

    @Autowired
    private SpotifyPlayer spotify_player;

    @Autowired
    private SpotifyApiAccesser api_accesser;

    public void initInitializer (SpotifyApiAccesser api_accesser, SpotifyPlayerState sps, SpotifyPlayer spotify_player) {
        this.sps = sps;
        this.spotify_player = spotify_player;
    }

    @Override
    public void initialize() {
        sps.initPlayStatus();
        Executor executor = new ScheduledThreadPoolExecutor(3);
        executor.execute(new PauserInitializer(sps, spotify_player));
        executor.execute(new PlaylistAndSongInitializer(api_accesser));
        executor.execute(new RefreshTokenInitializer(api_accesser));
    }
}