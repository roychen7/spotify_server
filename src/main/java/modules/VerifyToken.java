/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author roychen
 */
public class VerifyToken {
    ////        URL url = new URL("https://accounts.spotify.com/authorize?client_id=ba2aa172bb954f54be32398e8120381c&response_type=code&scope=user-modify-playback-state&redirect_uri=http://localhost:8080/callback");
////        HttpURLConnection con = (HttpURLConnection) url.openConnection();
////        con.setRequestMethod("GET");
//        HttpGet get = new HttpGet("https://accounts.spotify.com/authorize?client_id=ba2aa172bb954f54be32398e8120381c&response_type=code&scope=user-modify-playback-state&redirect_uri=http://localhost:8080/callback");
//        HttpClient client = HttpClients.createDefault();
//        
//        try {
//        HttpResponse response = client.execute(get);
//        HttpEntity entity =  response.getEntity();
//        return new ResponseEntity<String>(EntityUtils.toString(entity), headers, HttpStatus.ACCEPTED);
//        } catch (Error e) {
//            throw e;
//        }
    
    public VerifyToken() {
        
    }
    public static int verifyToken(String str) throws MalformedURLException, IOException {        
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/player");
        get.setHeader("Authorization", "Bearer " + str);
        int code = 0;
        
        try {
            HttpResponse response = client.execute(get);
            code = response.getStatusLine().getStatusCode();
            System.out.println("THE CODE OFFICIALLY IS " +Integer.toString(code));
        } catch (Error e) {
            throw e;
        }
        
        return code;
    }
}
