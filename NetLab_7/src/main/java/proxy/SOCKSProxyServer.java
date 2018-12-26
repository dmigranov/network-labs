package proxy;


import org.xbill.DNS.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SOCKSProxyServer
{
    private int port;
    private static final int bufSize = 8192;
    private DatagramChannel dnsServerChannel;
    private InetSocketAddress dnsServerAddress;
    private Map<String, ProxyContext> namesToBeResolved = new HashMap<>();
    SOCKSProxyServer(int port)
    {
        this.port = port;
        String dnsServers[] = ResolverConfig.getCurrentConfig().servers();
        try {
            dnsServerAddress = new InetSocketAddress(InetAddress.getByName(dnsServers[0]), 53);
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
            System.exit(10);
        }
    }

    public void run()
    {
        try (Selector selector = Selector.open();
             ServerSocketChannel local = ServerSocketChannel.open();
             DatagramChannel dnsServerChannel = DatagramChannel.open())
        {
            local.bind(new InetSocketAddress("localhost", port));
            local.configureBlocking(false);
            local.register(selector, SelectionKey.OP_ACCEPT);

            this.dnsServerChannel = dnsServerChannel;
            dnsServerChannel.connect(dnsServerAddress);
            dnsServerChannel.configureBlocking(false);
            dnsServerChannel.register(selector, SelectionKey.OP_READ);

            while (true)
            {
                selector.select();
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
        client.register(key.selector(), SelectionKey.OP_READ);  //подключиться к удалённому серверу, как в форвардере, пока не можеи (не знаем куда), потому ждём...
    }

    private void read(SelectionKey key) throws IOException
    {
        ByteBuffer buf = ByteBuffer.allocate(bufSize);
        Channel channel = key.channel();

        if (channel == dnsServerChannel)
        {
            processDNSResponse(buf, key);
        }
        else
        {
            SocketChannel keyChannel = (SocketChannel) key.channel();
            ProxyContext pc = (ProxyContext) key.attachment();
            if (pc == null) {
                pc = new ProxyContext(null, keyChannel);
                key.attach(pc);
            }
            int readCount = 0;
            try {
                if ((readCount = keyChannel.read(buf)) == -1) {
                    key.cancel(); //?
                    return;
                }
            } catch (IOException e) {
                key.cancel();
                return;
            }

            if (pc.getWhereToWrite() == null) {
                parseHeaders(buf, key);
            } else {
                SocketChannel remote = pc.getWhereToWrite();

                buf.flip();

                if (remote.isConnected() && readCount != 0) {
                    int writeCount = 0;
                    try {
                        writeCount = remote.write(buf);
                    }
                    catch(IOException e)
                    {
                        key.cancel();
                        return;
                    }
                    if (writeCount != readCount) {
                        System.out.println(readCount + " " + writeCount);
                        pc.setBuffer(buf);
                        remote.register(key.selector(), SelectionKey.OP_WRITE, pc);
                    } else {
                        buf.clear();
                        remote.register(key.selector(), SelectionKey.OP_READ, new ProxyContext(pc.getFromWhere(), pc.getWhereToWrite()));
                    }
                } else {
                    remote.register(key.selector(), SelectionKey.OP_CONNECT, new ProxyContext(buf, pc.getFromWhere(), pc.getWhereToWrite()));
                }
            }
        }
    }

    private void connect(SelectionKey key) throws ClosedChannelException, IOException
    {
        SocketChannel keyChannel = (SocketChannel) key.channel();

        if (!keyChannel.isConnected() && keyChannel.finishConnect()) {
            if (((ProxyContext) key.attachment()).getToWrite() != null)
                key.interestOps(SelectionKey.OP_WRITE);
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
            //это самое первое сообщение с методами аутентификации: сначала клиент посылает приветствие; у нас это 5 1 0, где 5 - версия, 1 - количество способов атентификации, 0 - без аутентификации
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.put(new byte[] {5, 0});
            bb.flip();
            int writeCount = keyChannel.write(bb);
        }
        else //connection request
        {
            //формат: 5 1 0 1 5 39 114 78 0 80; 5 - версия протокола; 1 - TCP/IP stream; 0 - reserves; 1 - IPv4 (в случае доменного имени тут будет 3); 5 39 114 78 - IP; 0 80 - порт; NB: если у файрфокса есть в кэше IP-адреса, то понятно, то понятно, что слать он будет их. Поэтому тестировать на новых сайтах!
            if(headerBytes[1] != 1) {

                return; //по условию задачи, поддерживаем только TCP
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

                byte[] response = new byte[] {5, 0, 0, 1, 0, 0, 0, 0, 0, 0}; //вместо нулей должен быть айпи и порт
                ByteBuffer bb = ByteBuffer.allocate(response.length);
                bb.put(response);
                bb.flip();
                int writeCount = keyChannel.write(bb);
            }
            else if(headerBytes[3] == 3) //доменное имя, резолвим
            {
                byte len = headerBytes[4];
                byte[] domainBytes = new byte[len];
                System.arraycopy(headerBytes, 5, domainBytes, 0, len);
                String domainName = new String(domainBytes, "UTF-8");
                short port = ByteBuffer.wrap(new byte[]{headerBytes[5 + len], headerBytes[5 + len + 1]}).getShort(); //?

                Message message = new Message();
                Record record = Record.newRecord(new Name(domainName + "."), Type.A, DClass.IN);
                message.addRecord(record, Section.QUESTION);
                message.getHeader().setFlag(Flags.RD);

                byte[] messageBytes = message.toWire();
                ByteBuffer bb = ByteBuffer.allocate(messageBytes.length);
                bb.put(messageBytes);
                bb.flip();
                dnsServerChannel.send(bb, dnsServerAddress);

                pc.setDestinationPort(port);
                namesToBeResolved.put(domainName, pc);
            }
        }
    }

    private void processDNSResponse(ByteBuffer buf, SelectionKey key) throws IOException
    {

        dnsServerChannel.read(buf); //а если прочитает не всё?
        Message message = new Message(buf.array());
        Record[] answer = message.getSectionArray(Section.ANSWER);

        String name = null, CNAMEAlias = null, CNAMEName = null;
        InetAddress inetAddress = null;

        //CName и A!
        for (Record r : answer)
        {
            if(r.getType() == Type.CNAME)
            {
                CNAMEName = r.getName().toString();
                if(name == null)
                    name = CNAMEName.substring(0, CNAMEName.length() - 1);;
                CNAMEAlias = ((CNAMERecord)r).getAlias().toString();

            }
        }
        for (Record r : answer)
        {
            if(r.getType() == Type.A)
            {
                String AAlias = r.getName().toString();

                if(CNAMEAlias != null && AAlias.equals(CNAMEAlias))
                {
                    inetAddress = ((ARecord) r).getAddress();
                    break;
                }
                else if (CNAMEAlias == null)
                {
                    name = r.getName().toString();
                    name = name.substring(0, name.length() - 1);
                    inetAddress = ((ARecord) r).getAddress();
                    break;
                }
            }
        }

        if (inetAddress == null)
        {
            Message messageBack = new Message();
            Record record = Record.newRecord(new Name(name + "."), Type.A, DClass.IN);
            message.addRecord(record, Section.QUESTION);
            message.getHeader().setFlag(Flags.RD);

            byte[] messageBytes = message.toWire();
            ByteBuffer bb = ByteBuffer.allocate(messageBytes.length);
            bb.put(messageBytes);
            bb.flip();
            dnsServerChannel.send(bb, dnsServerAddress);
            return;
        }


        byte[] addressBytes;
        //try {
            addressBytes = inetAddress.getAddress();
        /*}catch(NullPointerException e)
        {
            namesToBeResolved.remove(name);
            return;
        }*/

        Iterator<Map.Entry<String, ProxyContext>> it = namesToBeResolved.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, ProxyContext> e = it.next();
            if (e.getKey().equals(name))
            {
                SocketChannel remoteServer = SocketChannel.open();
                remoteServer.configureBlocking(false);

                ProxyContext pc = e.getValue();
                short port = pc.getDestinationPort();

                if (!remoteServer.connect(new InetSocketAddress(InetAddress.getByAddress(addressBytes), port)))
                {
                    remoteServer.register(key.selector(), SelectionKey.OP_CONNECT, new ProxyContext(remoteServer, pc.getFromWhere())); //?
                }
                pc.setWhereToWrite(remoteServer);

                byte[] response = new byte[] {5, 0, 0, 1, 0, 0, 0, 0, 0, 0}; //вместо нулей должен быть айпи и порт!
                ByteBuffer bb = ByteBuffer.allocate(response.length);
                bb.put(response);
                bb.flip();
                int writeCount = pc.getFromWhere().write(bb);

                it.remove();
            }
        }
    }
}

