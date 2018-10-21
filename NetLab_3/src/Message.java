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
    private SocketAddress dest = null;
    private boolean isOriginal;


    public Message(byte[] data, SocketAddress dest) {
        this.data = data;
        this.dest = dest;
        /*byte[] uuidData = new byte[data.length - 1];
        System.arraycopy(data, 1, uuidData,0, data.length - 1);*/
        isOriginal = true;
        UUID uuid = UUID.nameUUIDFromBytes(data); //including the first byte
        /*try {
            System.out.println("Constr: " + new String(data, "UTF-8"));
        }
        catch(UnsupportedEncodingException e)
        {}*/
        //System.out.println("Constr: " + Arrays.toString(data));
        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

    }

    /*public Message(byte[] data, SocketAddress source, SocketAddress dest) {
        this(data, dest);
        this.source = source;
        isOriginal = false;
    }*/

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
    int incrementCount()
    {
        tryCount++;
        return tryCount;
    }


    public SocketAddress getDest() {
        return dest;
    }
}