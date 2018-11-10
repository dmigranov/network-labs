package server;

import java.util.UUID;

public class Message {
    private static int counter = 0;
    private int id;
    private String message;
    private String author; //i guess a ref to User won't be optimal
    private boolean isSystem;


    public Message(String message, String username)
    {
        this.message = message;
        this.author = username;
        id = counter++;
        isSystem = false;
    }

    public Message(String message)
    {
        this.message = message;
        this.author = "root";
        id = counter++;
        isSystem = true;
    }

    public int getId() {
        return id;
    }
}