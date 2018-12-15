package server;

import io.undertow.websockets.core.WebSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class User {

    private String token;
    private String username;
    private boolean isOnline = true; //мож потом понадобится
    private int onlineCounter = 0;
    private WebSocketChannel webSocketChannel = null;
    private boolean wentOffline;


    public User(String username)
    {
        this.username = username;
        this.token = UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public void setOnlineCounter(int onlineCounter) {
        this.onlineCounter = onlineCounter;
    }

    public String getUsername() {
        return username;
    }


    public boolean isOnline() {
        return isOnline;
    }

    int incrementOnlineCounter()
    {
        return ++onlineCounter;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getToken() {
        return token;
    }

    public void setWebSocketChannel(WebSocketChannel wsc)
    {
        webSocketChannel = wsc;
    }

    public WebSocketChannel getWebSocketChannel() {
        return webSocketChannel;
    }

    public void setWentOffline(boolean wentOffline) {
        this.wentOffline = wentOffline;
    }
}

