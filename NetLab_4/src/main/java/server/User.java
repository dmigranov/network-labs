package server;

import java.util.UUID;

public class User {
    private static int counter = 0;
    private int id;
    private String username;
    //private boolean online; //мож потом понадобится
    private String token;

    public User(String username)
    {
        this.username = username;
        id = counter++;
        //online = true;
        token = UUID.randomUUID().toString();
    }
    
    public String getUsername() {
        return username;
    }


    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }


}

