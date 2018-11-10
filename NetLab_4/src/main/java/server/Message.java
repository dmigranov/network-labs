package server;

import java.util.UUID;

public class Message {
    private static int counter = 0;
    private int id;
    private String message;
    private String author; //i guess a ref to User won't be optimal


    public Message(String message, String username)
    {
        this.message = message;
        this.author = username;
        id = counter++;
    }

}