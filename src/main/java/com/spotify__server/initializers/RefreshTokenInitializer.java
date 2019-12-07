package com.spotify__server.initializers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.spotify__server.components.accessers.spotify_api_access.SpotifyApiAccesser;

public class RefreshTokenInitializer implements Initializer, Runnable {

    private SpotifyApiAccesser api_accesser;

    public RefreshTokenInitializer(SpotifyApiAccesser api_accesser) {
        this.api_accesser = api_accesser;
    }
    
    @Override
    public void run() {
        initialize();
    }

    @Override
    public void initialize() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new Refresh(), 60, TimeUnit.MINUTES);
    }
    
    class Refresh implements Runnable {

        public void run() {
            try {
                api_accesser.refreshToken();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (net.minidev.json.parser.ParseException e) {
                e.printStackTrace();
            }
        }
    }
}