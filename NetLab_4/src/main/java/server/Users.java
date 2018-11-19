package server;

import java.util.HashMap;
import java.util.Map;

public class Users{
    private int userCounter = 0;

    private Map<Integer, User> idUsers = new HashMap<>();
    //Map<String, User> tokenUsers = new HashMap<>();


    public int add(User user)
    {
        int currentCounter = userCounter++;
        idUsers.put(currentCounter, user);
        //String token = UUID.nameUUIDFromBytes(user.getUsername().getBytes(StandardCharsets.UTF_8)).toString();

                //fromString(user.getUsername()).toString();
        //tokenUsers.put(token, user);
        return currentCounter;
    }

    public int size() {
        return idUsers.size();
    }

    public User get(int uid) {
        return idUsers.get(uid);
    }

    public User get(String token)
    {
        for (int i = 0; i < idUsers.size(); i++)
        {
            User user = idUsers.get(i);
            user.setOnlineCounter(0);
            if (token.equals(user.getToken()))
                return user;
        }
        return null;
    }

    public Integer getIDByToken(String token)
    {
        for (int i = 0; i < idUsers.size(); i++)
        {
            User user = idUsers.get(i);
            if (token.equals(user.getToken()))
                return i;
        }
        return null;
    }

    public boolean containName(String username) {
        for (int i = 0; i < idUsers.size(); i++)
        {
            User user = idUsers.get(i);
            if (user.getUsername().equals(username))
                return true;
        }
        return false;
    }
}
