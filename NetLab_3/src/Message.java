import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Message {
    //private  uuid;
    private byte[] uuidBytes = new byte[16];
    private byte[] data ;
    private int tryCount = 0;
    public static final int maxTryCount = 5;
    private SocketAddress source  = null;
    private boolean isOriginal;


    public Message(byte[] data) {
        this.data = data;
        isOriginal = true;
        UUID uuid = UUID.nameUUIDFromBytes(data);
        //System.out.println(uuid);
        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

    }

    public Message(byte[] data, SocketAddress source) {
        this(data);
        this.source = source;
        isOriginal = false;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getUUIDBytes() {
        return uuidBytes;
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


}