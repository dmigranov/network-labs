package client;

import org.json.JSONArray;
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
    private String serverAddress;
    private String token;
    private int uid;
    private Users users;


    MessageRefresher(String serverAddress, String token, int uid, Users users)
    {
        this.serverAddress = serverAddress;
        this.token = token;
        this.uid = uid;
        this.users = users;
    }

    @Override
    public void run()  {

        while(true) {
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e) {}
            try {
                int messagesAccepted = 0;
                URL url = new URL(serverAddress + "/messages?offset=" + currentMessageID + "&count=25"); //не забывать считать скольк на самом деле!
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Token " + token);
                con.setRequestProperty("Host", "localhost");

                InputStream is = con.getInputStream();
                String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //мож по другому
                JSONArray messages = new JSONObject(body).getJSONArray("messages");
                for (int i = 0; i < messages.length(); i++)
                {
                    JSONObject msg = (JSONObject)messages.get(i);
                    int messageUid = msg.getInt("author");
                    String username;
                    if(messageUid != uid)
                    {
                        if(users.getUsers().containsKey(messageUid))
                            username = users.getUsers().get(messageUid);
                        else {
                            username = getNickname(messageUid);
                            if (username == null)
                                username = "User" + messageUid;
                        }
                        //TODO: выводить имя, а не ID! если есть в Users id, то выводить никнейм сразу, иначе - по методу обратиться к серверу и добавить в список
                        System.out.println(username +": " + msg.getString("message"));
                    }
                    messagesAccepted++;
                }
                currentMessageID += messagesAccepted;
                is.close();
            }
            catch(IOException e) {}
            //create a get /messages req


        }
    }

    private String getNickname(int uid) throws IOException {
        URL url = new URL(serverAddress + "/users/" + uid); //не забывать считать скольк на самом деле!
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Token " + token);
        con.setRequestProperty("Host", "localhost");
        if(con.getResponseCode() == 404)
            return null;
        InputStream is = con.getInputStream();
        String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //мож по другому
        return (String)new JSONObject(body).get("username");
    }
}
