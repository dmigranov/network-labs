package ru.nsu.migranov.portforwarder;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ForwarderContext {
    private ByteBuffer toWrite;
    private SocketChannel toServer;
    private SocketChannel toClient;

    ForwarderContext(ByteBuffer toWrite, SocketChannel toServer, SocketChannel toClient)
    {
        this.toWrite = toWrite;
        this.toServer = toServer;
        this.toClient = toClient;
    }

    ForwarderContext(SocketChannel toServer, SocketChannel toClient)
    {
        this.toWrite = null;
        this.toServer = toServer;
        this.toClient = toClient;
    }
    void setBuffer(ByteBuffer toWrite)
    {
        this.toWrite = toWrite;
    }

    public ByteBuffer getToWrite() {
        return toWrite;
    }


}
