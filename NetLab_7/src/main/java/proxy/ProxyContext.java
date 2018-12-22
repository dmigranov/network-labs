package proxy;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ProxyContext {
    private ByteBuffer toWrite;
    private SocketChannel whereToWrite;
    private SocketChannel fromWhere;

    ProxyContext() {}

    ProxyContext(ByteBuffer toWrite, SocketChannel whereToWrite, SocketChannel fromWhere)
    {
        this.toWrite = toWrite;
        this.whereToWrite = whereToWrite;
        this.fromWhere = fromWhere;
    }

    ProxyContext(SocketChannel whereToWrite, SocketChannel fromWhere)
    {
        this.toWrite = null;
        this.whereToWrite = whereToWrite;
        this.fromWhere = fromWhere;
    }
    void setBuffer(ByteBuffer toWrite)
    {
        this.toWrite = toWrite;
    }

    public ByteBuffer getToWrite() {
        return toWrite;
    }


    public SocketChannel getWhereToWrite() {
        return whereToWrite;
    }

    public SocketChannel getFromWhere() {
        return fromWhere;
    }

}
