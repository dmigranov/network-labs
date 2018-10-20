import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
        byte[] uuidData = new byte[data.length - 1];
        System.arraycopy(data, 1, uuidData,0, data.length - 1);
        isOriginal = true;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData);
        /*try {
            System.out.println("Constr: " + new String(uuidData, "UTF-8"));
        }
        catch(UnsupportedEncodingException e)
        {}*/
        System.out.println("Constr: " + Arrays.toString(uuidData));
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