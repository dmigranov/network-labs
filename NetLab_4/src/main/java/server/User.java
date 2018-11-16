package server;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class User {

    String token;
    private String username;
    private boolean isOnline = true; //мож потом понадобится
    private int onlineCounter = 0;



    User(String username)
    {
        this.username = username;
        this.token = UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)).toString();

        //online = true;
        //token = UUID.randomUUID().toString();
    }

    public void setOnlineCounter(int onlineCounter) {
        this.onlineCounter = onlineCounter;
    }

    public String getUsername() {
        return username;
    }


    /*public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }*/


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
}

