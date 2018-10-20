import java.util.UUID;

public class Message {
    private UUID uuid;
    private byte[] data;
    private int tryCount = 0;
    public static final int maxTryCount = 5;


    public Message(byte[] data) {
        this.data = data;
        UUID uuid = UUID.nameUUIDFromBytes(data);

    }

    public byte[] getData() {
        return data;
    }
}