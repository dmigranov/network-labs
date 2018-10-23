import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Message {
    private UUID uuid;
    private byte[] uuidBytes = new byte[16];
    private byte[] data ;
    private int tryCount = 0;
    public static final int maxTryCount = 10;
    private SocketAddress source  = null;
    private SocketAddress dest = null;
    private boolean isOriginal;


    public Message(byte[] data, SocketAddress dest) {
        this.data = data;
        this.dest = dest;
        /*byte[] uuidData = new byte[data.length - 1];
        System.arraycopy(data, 1, uuidData,0, data.length - 1);*/
        isOriginal = true;
        uuid = UUID.nameUUIDFromBytes(data); //including the first byte
        //System.out.println("Constr: " + uuid);
        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

    }

    public byte[] getData() {
        return data;
    }

    public UUID getUUID() {
        return uuid;
    }

    int incrementCount()
    {
        tryCount++;
        return tryCount;
    }

    public SocketAddress getDest() {
        return dest;
    }
}