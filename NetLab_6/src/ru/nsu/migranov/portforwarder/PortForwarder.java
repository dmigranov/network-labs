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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PortForwarder {
    private int lport;
    //private InetAddress rhost;
    //private int rport;
    private InetSocketAddress serverAddress;
    private Map<Integer, SocketChannel> users = new HashMap<>(); //мапа порт куда подключились - сокетченнел

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
                        }
                        clientKey.attach(remote);

                    }

                    if(key.isReadable())
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
                        SocketChannel remote = (SocketChannel)key.attachment();

                        //remote.write(buf);

                    }

                    if(key.isConnectable())
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
