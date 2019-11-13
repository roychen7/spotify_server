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

    // param str is access token, ánd the code checks the access token against an arbitrarily chosen spotify api call,
    // for the sole purpose of determining if the given access token is valid or not. Return data 
    // value is response code of api call
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
