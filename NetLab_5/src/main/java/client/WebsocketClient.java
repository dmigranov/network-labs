package client;

import org.json.JSONObject;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@ClientEndpoint
public class WebsocketClient implements Runnable
{
    String serverAddress;
    Session session = null;
    String token;
    int uid;
    Users users;

    WebsocketClient(String serverAddress, String token, int uid, Users users) throws IOException, DeploymentException, URISyntaxException
    {
        this.serverAddress = serverAddress;
        this.token = token;
        this.uid = uid;
        this.users = users;
        URI uri = new URI("ws" + serverAddress.substring(4) + "/messages_ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, uri);


    }
    @OnOpen
    public void onOpen(Session session) throws IOException
    {
        System.out.println("WEBSOCKET OPENED");
        this.session = session;
        session.getBasicRemote().sendText(token);

    }

    @OnMessage
    public void onMessage(Session session, String s) throws IOException
    {
        JSONObject msg = new JSONObject(s);
        int messageId = msg.getInt("id");
        int messageUid = msg.getInt("author");
        String username;
        if(messageUid != uid) {
            if (messageUid == -1)
                username = "System";
            else if (users.getUsers().containsKey(messageUid))
                username = users.getUsers().get(messageUid);
            else {
                username = getNickname(messageUid);
                if (username == null)
                    username = "User" + messageUid;
            }
            System.out.println(username + ": " + msg.getString("message"));
        }

    }

    @Override
    public void run() {

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
        is.close();
        users.getUsers().put(uid, (String)new JSONObject(body).get("username"));
        return (String)new JSONObject(body).get("username");
    }
}
