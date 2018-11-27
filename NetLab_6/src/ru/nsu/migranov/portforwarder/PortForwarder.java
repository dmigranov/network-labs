package ru.nsu.migranov.portforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PortForwarder {
    private int lport;
    private InetAddress rhost;
    private int rport;
    public PortForwarder(int lport, InetAddress rhost, int rport) {
        this.lport = lport;
        this.rhost = rhost;
        this.rport = rport;
    }


    public void run()
    {
        try(Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();) {
            ssc.bind(new InetSocketAddress("localhost", lport)); //название протокола?
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while(true)
            {
                selector.select(); //возвращает только если хотя бы один channel выбран
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while(iter.hasNext())
                {
                    System.out.println("HERE");
                }
            }

        }
        catch(IOException e)
        {
            System.err.println("IOException!");
            System.exit(2);
        }

    }



}
