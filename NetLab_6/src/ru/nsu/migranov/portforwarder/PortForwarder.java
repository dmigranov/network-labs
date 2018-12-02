package ru.nsu.migranov.portforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
            ServerSocketChannel local = ServerSocketChannel.open();
           )
        {
            local.bind(new InetSocketAddress("localhost", lport)); //название протокола?
            local.configureBlocking(false);
            local.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while(true)
            {
                selector.select(); //возвращает только если хотя бы один channel выбран
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while(iter.hasNext())
                {
                    SelectionKey key = iter.next();
                    if(key.isAcceptable())
                    {
                        SocketChannel client = local.accept();

                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ); //READ? WRITE
                        //TODO: при подключении клиента сервер открывает соединение с rhost : rport
                        SocketChannel remote = SocketChannel.open();
                        remote.configureBlocking(false);
                        if(!remote.connect(serverAddress)) {
                            remote.register(selector, SelectionKey.OP_CONNECT);
                        }
                        else {
                            remote.register(selector, SelectionKey.OP_READ); //я не регистрирую на Write т.к. на write доступен почти всегда; по требованию!
                            //System.out.println(remote.getLocalAddress() + " " + remote.getRemoteAddress());
                        }
                    }

                    if(key.isReadable())
                    {
                        SocketChannel keyChannel = (SocketChannel)key.channel(); //не обязательно наш клиент, целевой сервер тоже может
                        keyChannel.read(buf);
                        System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                        //if(если от сервера передать клиенту)
                        //если от клиента передать серверу
                        System.out.println(new String(buf.array(), Charset.forName("UTF-8")));
                    }

                    /f(key.isConnectable())
                    {
                        //stackoverflow java solaris nio op_connect problem
                        SocketChannel remote = (SocketChannel)key.channel();
                        if(remote.finishConnect())
                        {
                            //remote.getRemoteAddress() - адрес сервера если чё
                            key.interestOps(SelectionKey.OP_READ); //READ WRITE?
                            //System.out.println(remote.getLocalAddress() + " " + remote.getRemoteAddress());
                        }
                        continue; //не делаю ремув ь.к поменял
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
