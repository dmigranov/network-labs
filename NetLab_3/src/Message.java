import java.util.UUID;

public class Message {
    private UUID uuid;
    private byte[] text;


    public Message(byte[] text) {
        this.text = text;
        UUID uuid = UUID.nameUUIDFromBytes(text);
    }

}