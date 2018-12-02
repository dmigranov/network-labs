package ru.nsu.migranov.portforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class PortForwarder {
    private int lport;
    //private InetAddress rhost;
    //private int rport;
    private InetSocketAddress serverAddress;
    public PortForwarder(int lport, InetAddress rhost, int rport) {
        this.lport = lport;
        serverAddress = new InetSocketAddress(rhost, rport);
    }


    public void run()
    {
        try(Selector selector = Selector.open();
            ServerSocketChannel forwarder = ServerSocketChannel.open();
            SocketChannel serverChannel = SocketChannel.open())
        {
            forwarder.bind(new InetSocketAddress("localhost", lport)); //название протокола?
            forwarder.configureBlocking(false);
            forwarder.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while(true)
            {
                selector.select(); //возвращает только если хотя бы один channel выбран
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while(iter.hasNext())
                {
                    //System.out.println("HERE");
                    SelectionKey key = iter.next();

                    if(key.isAcceptable())
                    {
                        SocketChannel client = forwarder.accept();
                        //System.out.println(client.getLocalAddress() + " " + client.getRemoteAddress());
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ); //READ? WRITE
                        //при подключении клиента сервер открывает соединение с rhost : rport
                    }

                    if(key.isReadable())
                    {
                        SocketChannel sender = (SocketChannel)key.channel(); //не обязательно наш клиент, целевой сервер тоже может
                        sender.read(buf);
                        System.out.println(new String(buf.array(), Charset.forName("UTF-8")));
                        //SelectionKey receiverKey = forwarder.regis
                    }

                    //

                    iter.remove();
                }
            }

        }
        catch(IOException e)
        {
            System.err.println("IOException!");
            System.exit(2);
        }

    }

    private void readData(SelectionKey key, ByteBuffer buf) throws IOException
    {
    }

}
