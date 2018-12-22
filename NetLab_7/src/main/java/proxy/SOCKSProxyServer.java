package proxy;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SOCKSProxyServer
{
    private int port;
    private static final int bufSize = 1024;

    SOCKSProxyServer(int port)
    {
        this.port = port;
    }


    public void run()
    {
        try (Selector selector = Selector.open();
             ServerSocketChannel local = ServerSocketChannel.open())
        {
            local.bind(new InetSocketAddress("localhost", port));
            local.configureBlocking(false);
            local.register(selector, SelectionKey.OP_ACCEPT);

            while (true)
            {
                selector.select(); //возвращает только если хотя бы один channel выбран
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext())
                {
                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isConnectable()) {
                        connect(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                    iter.remove();
                }
            }


        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void accept(SelectionKey key) throws ClosedChannelException, IOException
    {
        SocketChannel client = ((ServerSocketChannel)key.channel()).accept();
        client.configureBlocking(false);
        //подключиться к удалённому серверу, как в форвардере, пока не можеи, потому ждём...
        client.register(key.selector(), SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException
    {
        ByteBuffer buf = ByteBuffer.allocate(bufSize);
        SocketChannel keyChannel = (SocketChannel) key.channel();

        ProxyContext fc = (ProxyContext) key.attachment();

        if(fc == null)
        {
            fc = new ProxyContext(null, keyChannel);
            key.attach(fc);
        }

        int readCount;
        if ((readCount = keyChannel.read(buf)) == -1) {
            return;
        }

        if (fc.getWhereToWrite() == null)
        {
            //читаем заголовки
            //и отвечаем клиенту
            byte[] headerBytes = buf.array();
            int count = buf.position();
            /*for (int i = 0; i < count; i++)
                System.out.print(headerBytes[i] + " ");
            System.out.println();*/
            //сначала клиент посылает приветствие; у нас это 5 1 0, где 5 - версия, 1 - количество способов атентификации, 0 - без аутентификации
        }
        else
        {
            //как в форвардере
        }






    }

    private void connect(SelectionKey key) {

    }

    private void write(SelectionKey key) {

    }
}
