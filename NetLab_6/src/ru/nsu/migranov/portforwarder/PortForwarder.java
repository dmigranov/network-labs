package ru.nsu.migranov.portforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PortForwarder {
    private int lport;
    private SocketAddress serverAddress;
    private Map<SocketAddress, SocketChannel> usersServer = new HashMap<>(); //мапа: адрес пользователя - сокетченнел от нас до сервера
    private Map<SocketAddress, SocketChannel> usersUs = new HashMap<>(); //мапа: адрес пользователя - сокетченнел от нас до юзера


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
                            remote.register(selector, SelectionKey.OP_WRITE); //я не регистрирую на Write т.к. на write доступен почти всегда; по требованию!
                            //users.put()
                        }
                        //System.out.println(remote.getLocalAddress() + " " + remote.getRemoteAddress());
                        //System.out.println(client.getLocalAddress() + " " + client.getRemoteAddress());
                        usersServer.put(client.getRemoteAddress(), remote);
                        usersUs.put(client.getRemoteAddress(), client);
                    }
                    else if(key.isReadable())
                    {
                        SocketChannel remote = null;
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                        try {
                            if(keyChannel.read(buf) == -1) {
                                keyChannel.close();
                                iter.remove(); //???
                                continue;
                            }
                            //System.out.println(new String(buf.array(), Charset.forName("UTF-8")));

                            remote = usersServer.get(keyChannel.getRemoteAddress());

                            if (remote != null && remote.getRemoteAddress().equals(serverAddress)) {
                                //System.out.println(remote.getLocalAddress() + " " + remote.getRemoteAddress());
                                //клиент пишет на сервер
                                buf.flip();
                                if (remote.isConnected()) {

                                    remote.write(buf);//проверка что не все записало; если не все то write
                                    remote.register(selector, SelectionKey.OP_READ, null);
                                } else {
                                    remote.register(selector, SelectionKey.OP_CONNECT, buf);
                                }
                            } else {
                                //сервер отвечает клиенту
                                System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                                remote = findUserSocketChannel(keyChannel.getLocalAddress());

                                buf.flip();

                                remote.write(buf);//проверка что не все записало; если не все то write
                                remote.register(selector, SelectionKey.OP_READ, null);
                                //remote.close(); //!!!!!!!
                            }
                        }
                        catch(IOException e)
                        {
                            System.out.println("HERE");
                            System.out.println(remote.getLocalAddress());
                            //delete?
                            //key.cancel();
                            //continue;

                        }
                    }

                    else if(key.isConnectable())
                    {
                        //stackoverflow java solaris nio op_connect problem
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                        /*if(keyChannel.isConnected())
                        {
                            keyChannel.close(); //закомментить?
                        }
                        else {
                            if (keyChannel.finishConnect()) {
                                key.interestOps(SelectionKey.OP_WRITE); //READ WRITE?
                                System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());   //remote.getRemoteAddress() - адрес сервера
                            }
                            //continue;
                        }*/
                        if(!keyChannel.isConnected() && keyChannel.finishConnect())
                        {
                            key.interestOps(SelectionKey.OP_WRITE);
                            System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                        }
                        /*else
                            keyChannel.close();*/
                    }
                    else if(key.isWritable())
                    {
                        ByteBuffer toWrite = (ByteBuffer)key.attachment();
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                        if(toWrite != null) //!!!
                        {
                            keyChannel.write(toWrite); //проверка на то что не все
                        }
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    iter.remove();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(2);
        }

    }


    private SocketChannel findUserSocketChannel(SocketAddress serverSocketAddress) throws IOException
    {
        for(Map.Entry<SocketAddress, SocketChannel> entry: usersServer.entrySet())
        {
            if (entry.getValue().isConnected() && entry.getValue().getLocalAddress().equals(serverSocketAddress)) {

                return usersUs.get(entry.getKey());
            }
        }
        return null;
    }

}
