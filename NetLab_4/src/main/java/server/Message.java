package server;

import java.util.UUID;

public class Message {
    private static int counter = 0;
    private int id;
    private String message;
    private int authorID; //i guess a ref to User won't be optimal
    private boolean isSystem;


    public Message(String message, int authorID)
    {
        this.message = message;
        this.authorID = authorID;
        id = counter++;
        isSystem = false;
    }

    public Message(String message)
    {
        this.message = message;
        this.authorID = -1;
        id = counter++;
        isSystem = true;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getAuthorID() {
        return authorID;
    }
}