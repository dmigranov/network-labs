package client;



import java.util.HashMap;
import java.util.Map;

public class Users {
    private Map<Integer, String> users = new HashMap<>(); //ключ - id, значение - ник

    public Map<Integer, String> getUsers() {
        return users;
    }

}
