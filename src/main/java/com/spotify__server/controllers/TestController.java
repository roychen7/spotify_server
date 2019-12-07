/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify__server.controllers;

import com.spotify__server.components.accessers.database_access.DatabaseAccesser;
import com.spotify__server.enums.PlaylistGenStatus;
import com.spotify__server.modules.Song;
import com.spotify__server.utils.WeightedRandomGenerator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author roychen
 */

@RestController
public class TestController {
    
//     @GetMapping("/testplay")
//    public ResponseEntity initBiasedPlaylist() throws SQLException, IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
//        
//        String access_token = DatabaseAccesser.getAccessToken();
//        HttpPut put = new HttpPut("https://api.spotify.com/v1/me/player/play");
//        HttpClient client = HttpClients.createDefault();
//        put.addHeader("Authorization", "Bearer " + access_token);
//        
//        List<NameValuePair> params = new ArrayList<>(2);
//        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
//        
//        String[] arr = new String[2];
//        params.add(new BasicNameValuePair("uris", arr));
//        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//        
//        client.execute(put);
//        return new ResponseEntity<>("asdf", headers, HttpStatus.ACCEPTED);
//    }
//}

// @GetMapping("/testselect")
// public ResponseEntity getSongs() {
//         try {
//             List<String> res_list = DatabaseAccesser.getListFromDb(3,
//                     "select `song_name`, `playlist_name`, `song_uri` from `songs`;");
//         } catch (SQLException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
// }
}
