package server;

public class User {
    private static int counter = 0;
    private int id;
    private String username;
    //private boolean online; //мож потом понадобится
    private long token;

    public User(String username)
    {
        this.username = username;
        id = counter++;
        //online = true;
        token = 2345; //TODO: исправить на нормально генерируемый токен!
    }
    
    public String getUsername() {
        return username;
    }


    public int getId() {
        return id;
    }

    public long getToken() {
        return token;
    }
}

