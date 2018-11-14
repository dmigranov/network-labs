package server;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class Messages {
    private int messageCounter = 0;
    private NavigableMap<Integer, Message> messages;

    Messages()
    {
        messages = new TreeMap<>();
    }

    private Messages(SortedMap<Integer, Message> messages)
    {
        this.messages = new TreeMap<>(messages);
    }

    public int add(Message msg) {
        int currentCounter = messageCounter++;
        messages.put(currentCounter, msg);
        return currentCounter;
    }


    public int size()
    {
        return messages.size();
    }

    public Messages subMessages(int fromKey, int toKey)
    {
        SortedMap<Integer, Message> sub = messages.subMap(fromKey, toKey);
        return new Messages(sub);

    }

    public Message get(int id) {
        return messages.get(id);
    }
}