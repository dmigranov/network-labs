package server;

public class Message {
    //private static int counter = 0; //maybe not the best system
    //private int id;
    private String message;
    private int authorID; //i guess a ref to User won't be optimal
    private boolean isSystem;


    Message(String message, int authorID)
    {
        this.message = message;
        this.authorID = authorID;
        //id = counter++;
        isSystem = false;
    }

    Message(String message)
    {
        this.message = message;
        this.authorID = -1;
        //id = counter++;
        isSystem = true;
    }

    /*public int getId() {
        return id;
    }*/

    public String getMessage() {
        return message;
    }

    public int getAuthorID() {
        return authorID;
    }
}