package server;

public class User {
    private static int counter = 0;
    private int id;
    private String username;
    private boolean isOnline = true; //мож потом понадобится
    private int onlineCounter = 0;



    User(String username)
    {
        this.username = username;
        id = counter++;
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
}

