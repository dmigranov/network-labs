package server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Users {
    //private int userCounter = 0;

    //Map<Integer, User> idUsers = new HashMap<>();
    Map<String, User> tokenUsers = new HashMap<>();


    String add(User user)
    {
        //int currentCounter = userCounter++;
        //idUsers.put(currentCounter, user);
        String token = UUID.randomUUID().toString();
        tokenUsers.put(token, user);
        return token;
    }
}
