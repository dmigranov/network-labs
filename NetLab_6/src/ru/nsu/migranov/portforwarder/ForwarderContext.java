package ru.nsu.migranov.portforwarder;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ForwarderContext {
    private ByteBuffer toWrite;
    private SocketChannel whereToWrite;
    //private SocketChannel fromWhere;

    ForwarderContext(ByteBuffer toWrite, SocketChannel whereToWrite)
    {
        this.toWrite = toWrite;
        this.whereToWrite = whereToWrite;
        //this.fromWhere = fromWhere;
    }

    ForwarderContext(SocketChannel whereToWrite)
    {
        this.toWrite = null;
        this.whereToWrite = whereToWrite;
        //this.fromWhere = fromWhere;
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

    /*public SocketChannel getFromWhere() {
        return fromWhere;
    }*/
}
