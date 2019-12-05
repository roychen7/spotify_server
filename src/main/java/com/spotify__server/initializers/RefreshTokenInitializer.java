package com.spotify__server.initializers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.spotify__server.utils.SpotifyApiCaller;

public class RefreshTokenInitializer implements Initializer {

    @Override
    public void initialize() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new Refresh(), 60, TimeUnit.MINUTES);
    }
    
    class Refresh implements Runnable {

        public void run() {
            try {
                SpotifyApiCaller.refreshToken();
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