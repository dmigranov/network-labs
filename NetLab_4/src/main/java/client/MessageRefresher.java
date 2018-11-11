package client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class MessageRefresher implements Runnable {
    private int currentMessageID = 0;
    private static final int count = 25;
    //URL url;
    String serverAddress;
    String token;


    MessageRefresher(String serverAddress, String token)
    {
        this.serverAddress = serverAddress;
        this.token = token;
    }


    @Override
    public void run()  {

        while(true) {
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e) {}
            try {
                URL url = new URL(serverAddress + "/messages?offset=" + currentMessageID + "&count=25"); //не забывать считать скольк на самом деле!
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Token " + token);
                con.setRequestProperty("Host", "localhost");

                InputStream is = con.getInputStream();
                String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //мож по другому
                System.out.println(body);
                //прибавить колво сообщений к cmID!
                JSONObject loginInfo = new JSONObject(body);
                is.close();
            }
            catch(IOException e) {}
            //create a get /messages req


        }
    }
}
