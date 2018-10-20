import java.net.SocketAddress;
import java.util.UUID;

public class Message {
    private UUID uuid;
    private byte[] data;
    private int tryCount = 0;
    public static final int maxTryCount = 5;
    private SocketAddress source  = null;
    private boolean isOriginal;


    public Message(byte[] data) {
        this.data = data;
        uuid = UUID.nameUUIDFromBytes(data);
        isOriginal = true;

    }

    public Message(byte[] data, SocketAddress source) {
        this(data);
        this.source = source;
        isOriginal = false;
    }

    public byte[] getData() {
        return data;
    }

    public SocketAddress getSource() {
        return source;
    }

    public boolean isOriginal() {
        return isOriginal;
    }
    public void incrementCount()
    {
        tryCount++;
    }

    public UUID getUUID() {
        return uuid;
    }
}