package proxy;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ProxyContext {
    private ByteBuffer toWrite;
    private SocketChannel whereToWrite;
    private SocketChannel fromWhere;
    private short destinationPort;
    //private String domainName = null;

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

    public void setWhereToWrite(SocketChannel whereToWrite) {
        this.whereToWrite = whereToWrite;
    }

    public SocketChannel getFromWhere() {
        return fromWhere;
    }

    public short getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(short destinationPort) {
        this.destinationPort = destinationPort;
    }

    /*public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }*/
}
