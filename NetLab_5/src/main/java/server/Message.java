package server;

public class Message {
    private String message;
    private int authorID;
    //private boolean isSystem;


    public Message(String message, int authorID)
    {
        this.message = message;
        this.authorID = authorID;
        //isSystem = false;
    }



    public String getMessage() {
        return message;
    }

    public int getAuthorID() {
        return authorID;
    }
}