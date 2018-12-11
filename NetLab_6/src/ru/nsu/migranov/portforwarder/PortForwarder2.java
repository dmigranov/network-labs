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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PortForwarder2 {
    private int lport;
    private SocketAddress serverAddress;

    PortForwarder2(int lport, InetAddress rhost, int rport) {
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


                        SocketChannel remote = SocketChannel.open();
                        remote.configureBlocking(false);
                        if(!remote.connect(serverAddress)) {
                            remote.register(selector, SelectionKey.OP_CONNECT, new ForwarderContext(remote, client));
                        }
                        else {
                            //remote.register(selector, SelectionKey.OP_WRITE, new ForwarderContext(remote, client));
                        }
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ, new ForwarderContext(remote, client));

                    }
                    else if(key.isReadable())
                    {
                        SocketChannel remote = null;
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                        try {
                            if(keyChannel.read(buf) == -1) {
                                keyChannel.close();
                                iter.remove();
                                continue;
                            }
                            //System.out.println(new String(buf.array(), Charset.forName("UTF-8")));
                            //ForwarderContext = key.
                            //remote = usersServer.get(keyChannel.getRemoteAddress());
                            ForwarderContext fc = (ForwarderContext)key.attachment();

                            if (fc.getWhereToWrite().getRemoteAddress().equals(serverAddress)) {

                                remote = fc.getWhereToWrite();
                                //клиент пишет на сервер
                                buf.flip();
                                if (remote.isConnected()) {

                                    remote.write(buf);//проверка что не все записало; если не все то write
                                    remote.register(selector, SelectionKey.OP_READ, new ForwarderContext(fc.getFromWhere(), fc.getWhereToWrite()));
                                } else {
                                    //remote.register(selector, SelectionKey.OP_CONNECT, new ForwarderContext(buf, fc.getFromWhere(), fc.getWhereToWrite()));
                                    remote.register(selector, SelectionKey.OP_CONNECT, new ForwarderContext(buf, fc.getWhereToWrite(), fc.getFromWhere()));

                                }
                            } else {
                                //сервер отвечает клиенту
                                //System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                                remote = fc.getWhereToWrite();

                                buf.flip();

                                remote.write(buf);//проверка что не все записало; если не все то write
                                remote.register(selector, SelectionKey.OP_READ, new ForwarderContext(fc.getFromWhere(), fc.getWhereToWrite()));
                                //remote.close(); //!!!!!!!
                            }
                            /*remote = fc.getWhereToWrite();
                            buf.flip();
                            if (remote.isConnected()) {

                                remote.write(buf);
                                remote.register(selector, SelectionKey.OP_READ, new ForwarderContext(fc.getFromWhere(), fc.getWhereToWrite()));
                            } else {
                                remote.register(selector, SelectionKey.OP_CONNECT, new ForwarderContext(buf, fc.getFromWhere(), fc.getWhereToWrite()));
                            }*/


                        }
                        catch(IOException e)
                        {

                            //continue;
                        }
                    }

                    else if(key.isConnectable())
                    {

                        SocketChannel keyChannel = (SocketChannel)key.channel();

                        if(!keyChannel.isConnected() && keyChannel.finishConnect())
                        {
                            if(((ForwarderContext)key.attachment()).getToWrite() != null)
                                key.interestOps(SelectionKey.OP_WRITE);
                            else
                                key.interestOps(0);
                            //System.out.println(keyChannel.getLocalAddress() + " " + keyChannel.getRemoteAddress());
                        }
                        /*else
                            keyChannel.close();*/
                    }
                    else if(key.isWritable())
                    {
                        ForwarderContext fc = (ForwarderContext)key.attachment();
                        ByteBuffer toWrite = fc.getToWrite();
                        SocketChannel keyChannel = (SocketChannel)key.channel();
                        SocketChannel wtwChannel;

                        if(toWrite != null) //!!!
                        {
                            keyChannel.write(toWrite); //проверка на то что не все
                            //fc.getWhereToWrite().write(toWrite);
                            wtwChannel = fc.getWhereToWrite();
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
        /*for(Map.Entry<SocketAddress, SocketChannel> entry: usersServer.entrySet())
        {
            if (entry.getValue().isConnected() && entry.getValue().getLocalAddress().equals(serverSocketAddress)) {

                return usersUs.get(entry.getKey());
            }
        }*/
        return null;
    }

}
