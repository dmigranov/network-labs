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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PortForwarder {
    private int lport;
    private SocketAddress serverAddress;
    private Map<SocketAddress, SocketChannel> users = new HashMap<>(); //мапа: адрес пользователя - сокетченнел от нас до сервера

    PortForwarder(int lport, InetAddress rhost, int rport) {
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
                buf.clear();
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
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ); //READ? WRITE
                        SocketChannel remote = SocketChannel.open();


                        remote.configureBlocking(false);
                        if(!remote.connect(serverAddress)) {
                            remote.register(selector, SelectionKey.OP_CONNECT);
                        }
                        else {
                            remote.register(selector, SelectionKey.OP_READ); //я не регистрирую на Write т.к. на write доступен почти всегда; по требованию!
                            //users.put()
                        }
                        //System.out.println(remote.getLocalAddress() + " " + remote.getRemoteAddress());
                        //System.out.println(client.getLocalAddress() + " " + client.getRemoteAddress());
                        users.put(client.getRemoteAddress(), remote);

                    }

                    else if(key.isReadable())
                    {
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                         //не обязательно наш клиент, целевой сервер тоже может
                        if(keyChannel.read(buf) == -1) {
                            keyChannel.close();
                            continue;
                        }

                        //пока не знаю как буду узнвавать, кому перенаправлять, но если что можно прикрепить объект к ключу
                        //System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                        //if(если от сервера передать клиенту)
                        //если от клиента передать серверу
                        System.out.println(new String(buf.array(), Charset.forName("UTF-8")));
                        SocketChannel remote = users.get(keyChannel.getRemoteAddress());
                        if(remote.getRemoteAddress().equals(serverAddress))
                        {
                            //клиент пишет на сервер
                            if(remote.isConnected())
                                ;
                            else {
                                remote.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT, buf);
                            }
                        }


                    }

                    else if(key.isConnectable())
                    {
                        //stackoverflow java solaris nio op_connect problem
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                        if(keyChannel.isConnected())
                        {
                            keyChannel.close();
                        }
                        else {
                            if (keyChannel.finishConnect()) {
                                //remote.getRemoteAddress() - адрес сервера если чё
                                key.interestOps(SelectionKey.OP_READ); //READ WRITE?
                                System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                            }
                            continue; //не делаю ремув ь.к поменял
                        }
                    }
                    //else if(key.isWritable())

                    //

                    iter.remove();
                }
            }

        }
        catch(IOException e)
        {
            //System.err.println("IOException!");
            e.printStackTrace();
            System.exit(2);
        }

    }

    private void readData(SelectionKey key, ByteBuffer buf) throws IOException
    {
    }

}
