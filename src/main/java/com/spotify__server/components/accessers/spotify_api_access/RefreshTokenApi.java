package com.spotify__server.components.accessers.spotify_api_access;

import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.components.accessers.database_access.PlaylistDatabaseAccesser;
import com.spotify__server.components.data.Data;
import com.spotify__server.utils.HelperClass;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

@Component
public class RefreshTokenApi {

    @Autowired
    private DatabaseAccesser database_accesser;

    private HttpClient client = HttpClients.createDefault();

    // calls refreshToken once access token has expired, and places it into the database
    public void refreshToken() throws FileNotFoundException, SQLException, IOException, ParseException {
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");

        String refresh_token = database_accesser.getSingleFromDb("select `refresh_token` from `token`");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", refresh_token));

        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        post.addHeader("Authorization",
                "Basic YmEyYWExNzJiYjk1NGY1NGJlMzIzOThlODEyMDM4MWM6MzI2ZGIwM2E2ODQwNGUwYWIwODhjYWNjMDZlYzU4OTY=");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse resp = client.execute(post);

        HttpEntity entity = resp.getEntity();
        String jsonString = HelperClass.getResponseString(entity);

        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(jsonString);

        String access_token = (String) jsonObj.get("access_token");
        database_accesser.updateIntoDb("update `token` set `access_token`='" + access_token + "'");
        database_accesser.updateAccessToken();
        System.out.println("Token::/refresh: updated token!");
    }
}