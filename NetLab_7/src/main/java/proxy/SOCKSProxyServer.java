package proxy;


import java.io.IOException;
import java.net.InetAddress;
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

        ProxyContext pc = (ProxyContext) key.attachment();
        if(pc == null)
        {
            pc = new ProxyContext(null, keyChannel);
            key.attach(pc);
        }

        int readCount;
        if ((readCount = keyChannel.read(buf)) == -1) {
            return;
        }
        System.out.println(readCount + " " + keyChannel.getRemoteAddress() + " " + keyChannel.getLocalAddress());

        if (pc.getWhereToWrite() == null)
        {
            parseHeaders(buf, key);
        }
        else
        {
            System.out.println("HERE");
        }
    }

    private void connect(SelectionKey key) throws ClosedChannelException, IOException
    {
        SocketChannel keyChannel = (SocketChannel) key.channel();

        if (!keyChannel.isConnected() && keyChannel.finishConnect()) {
            if (((ProxyContext) key.attachment()).getToWrite() != null)
                key.interestOps(SelectionKey.OP_WRITE);
            else
                key.cancel();
        }

    }

    private void write(SelectionKey key) throws IOException
    {
        ProxyContext pc = (ProxyContext) key.attachment();
        ByteBuffer toWrite = pc.getToWrite();
        SocketChannel keyChannel = (SocketChannel) key.channel();

        if (toWrite != null) //!!!
        {
            keyChannel.write(toWrite);
            toWrite.clear();
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private void parseHeaders(ByteBuffer buf, SelectionKey key) throws IOException//читаем заголовки и отвечаем клиенту
    {
        ProxyContext pc = (ProxyContext) key.attachment();
        SocketChannel keyChannel = (SocketChannel) key.channel();
        byte[] headerBytes = buf.array();
        int byteCount = buf.position();

        int authenticationMethodsCount = headerBytes[1];
        if(headerBytes[0] != 5)
        {
            //не та версия
            return;
        }

        if(authenticationMethodsCount == byteCount - 2)
        {
            //это самое первое сообщение с методами аутентификации
            //сначала клиент посылает приветствие; у нас это 5 1 0, где 5 - версия, 1 - количество способов атентификации, 0 - без аутентификации
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.put(new byte[] {5, 0});
            bb.flip();
            int writeCount = keyChannel.write(bb); //TODO: возможно (но маловероятно), что-то не запишется, поэтому возможно стоит поставить в селектор
        }
        else //connection request
        {
            for (int i = 0; i < byteCount; i++)
                System.out.print(headerBytes[i] + " ");
            System.out.println();
            //формат: 5 1 0 1 5 39 114 78 0 80; 5 - версия протокола; 1 - TCP/IP stream; 0 - reserves; 1 - IPv4 (в случае доменного имени тут будет 3); 5 39 114 78 - IP; 0 80 - порт; NB: если у файрфокса есть в кэше IP-адреса, то понятно, то понятно, что слать он будет их. Поэтому тестировать на новых сайтах!
            if(headerBytes[1] != 1) {
                //TODO: ответ клиенту!
                return; //поддерживаем только TCP
            }

            if(headerBytes[3] == 1) //IPv4, подключаемся туда
            {
                SocketChannel remoteServer = SocketChannel.open();
                remoteServer.configureBlocking(false);

                byte[] address = new byte[4];
                short port = ByteBuffer.wrap(new byte[]{headerBytes[8], headerBytes[9]}).getShort();
                System.arraycopy(headerBytes, 4, address, 0, 4);
                if (!remoteServer.connect(new InetSocketAddress(InetAddress.getByAddress(address), port)))
                {
                    remoteServer.register(key.selector(), SelectionKey.OP_CONNECT, new ProxyContext(remoteServer, keyChannel)); //
                }
                pc.setWhereToWrite(remoteServer);

                byte[] response = new byte[] {5, 0, 0, 1, 0, 0, 0, 0, 0, 0}; //вместо нулей должен быть айпи и порт!
                ByteBuffer bb = ByteBuffer.allocate(response.length);
                bb.put(response);
                bb.flip();
                int writeCount = keyChannel.write(bb);
            }
            else if(headerBytes[3] == 3) //доменное имя, резолвим
            {
                //TODO: resolve and ...
            }

        }
    }
}

