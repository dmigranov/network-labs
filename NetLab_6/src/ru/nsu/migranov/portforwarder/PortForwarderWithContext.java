package ru.nsu.migranov.portforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class PortForwarderWithContext {
    private int lport;
    private SocketAddress serverAddress;

    PortForwarderWithContext(int lport, InetAddress rhost, int rport) {
        this.lport = lport;
        serverAddress = new InetSocketAddress(rhost, rport);
    }

    public void run() {
        try (Selector selector = Selector.open();
             ServerSocketChannel local = ServerSocketChannel.open()
        ) {
            local.bind(new InetSocketAddress("localhost", lport)); //название протокола?
            local.configureBlocking(false);
            local.register(selector, SelectionKey.OP_ACCEPT);
            //ByteBuffer buf = ByteBuffer.allocate(1024);

            while (true) {
                //buf.clear();
                selector.select(); //возвращает только если хотя бы один channel выбран
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if (key.isAcceptable())
                    {
                        accept(key);
                    }
                    else if (key.isReadable()) {
                        read(key);
                    }
                    else if (key.isConnectable())
                    {
                        connect(key);
                    }
                    else if (key.isWritable())
                    {
                        write(key);
                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void accept(SelectionKey key)  throws ClosedChannelException, IOException
    {
        SocketChannel client = ((ServerSocketChannel)key.channel()).accept();

        client.configureBlocking(false);


        SocketChannel remote = SocketChannel.open();
        remote.configureBlocking(false);
        if (!remote.connect(serverAddress)) {
            remote.register(key.selector(), SelectionKey.OP_CONNECT, new ForwarderContext(remote, client));
        } else {
            //remote.register(selector, SelectionKey.OP_WRITE, new ForwarderContext(remote, client));

        }
        client.register(key.selector(), SelectionKey.OP_READ, new ForwarderContext(remote, client));
    }

    private void connect(SelectionKey key) throws ClosedChannelException, IOException
    {
        SocketChannel keyChannel = (SocketChannel) key.channel();

        if (!keyChannel.isConnected() && keyChannel.finishConnect()) {
            if (((ForwarderContext) key.attachment()).getToWrite() != null)
                key.interestOps(SelectionKey.OP_WRITE);
            else
                key.cancel();
            //System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
        }
                        /*else
                            keyChannel.close();*/
    }

    private void write(SelectionKey key) throws IOException
    {
        ForwarderContext fc = (ForwarderContext) key.attachment();
        ByteBuffer toWrite = fc.getToWrite();
        SocketChannel keyChannel = (SocketChannel) key.channel();

        if (toWrite != null) //!!!
        {
            keyChannel.write(toWrite);
            toWrite.clear();
        }
        key.interestOps(SelectionKey.OP_READ);
    }



    private void read(SelectionKey key) throws IOException
    {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        SocketChannel remote = null;
        SocketChannel keyChannel = (SocketChannel) key.channel();
        int readCount;
        try {
            if ((readCount = keyChannel.read(buf)) == -1) {

                return;
            }

            ForwarderContext fc = (ForwarderContext) key.attachment();

            remote = fc.getWhereToWrite();
            buf.flip();
            if (remote.isConnected() && readCount != 0) {

                int writeCount = remote.write(buf);

                System.out.println(readCount + " " + writeCount);
                if(writeCount != readCount) {

                    remote.register(key.selector(), SelectionKey.OP_WRITE, new ForwarderContext(fc.getFromWhere(), fc.getWhereToWrite()));
                }
                else {
                    buf.clear();
                    remote.register(key.selector(), SelectionKey.OP_READ, new ForwarderContext(fc.getFromWhere(), fc.getWhereToWrite()));
                }
            } else {
                remote.register(key.selector(), SelectionKey.OP_CONNECT, new ForwarderContext(buf, fc.getFromWhere(), fc.getWhereToWrite()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





//как только приконнекктились