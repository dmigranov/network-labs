

import java.io.IOException;
import java.net.*;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

public class TreeNode
{
    private DatagramSocket socket = null; //shall be closed in CR or CW

    private String nodeName;
    private int lossQuota;
    private int ownPort;
    private InetSocketAddress parentAddress = null;
    //private List<InetSocketAddress> children = new ArrayList<>();
    private Set<InetSocketAddress> children = new CopyOnWriteArraySet<>();
    private boolean isRoot = true;
    private Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private Queue<Message> sentMessages = new ConcurrentLinkedQueue<>();


    public final static byte childByte = 100;
    public final static byte msgByte = 10;
    public final static byte childAck = 101;
    public final static byte msgAck = 11;

    public TreeNode(String nodeName, int lossQuota, int ownPort)
    {
        this.nodeName = nodeName;
        //this.lossQuota = -1; //=without losses
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;

        try
        {
            socket = new DatagramSocket(ownPort);
        }
        catch(SocketException e)
        {
            System.err.println(e.getMessage());
            System.exit(3);
        }
    }

    public TreeNode(String nodeName, int lossQuota, int ownPort, InetAddress parentIP, int parentPort)
    {
        this(nodeName, lossQuota, ownPort);
        isRoot = false;
        parentAddress = new InetSocketAddress(parentIP, parentPort);

        notifyParent();
    }
    private void notifyParent()
    {
        byte[] msg = new byte[1];
        msg[0] = childByte;
        DatagramPacket packet = new DatagramPacket(msg, msg.length, parentAddress);

        try {
            socket.setSoTimeout(5000);
            for(int i = 0; i < 5; i++)
                socket.send(packet);
            DatagramPacket answer = new DatagramPacket(new byte[1], 1);
            socket.receive(answer);
            if(answer.getData()[0] != childAck)
                throw new IOException();
        }
        catch(IOException e)
        {
            System.out.println("Can't connect to a parent! The node is considered a root now");
            isRoot = true;
        }
    }

    public void addChild(InetSocketAddress childAddress)
    {
        children.add(childAddress);
    }

    public Set<InetSocketAddress> getChildrenAddresses()
    {
        return children;
    }

    public boolean isRoot()
    {
        return isRoot;
    }

    public InetSocketAddress getParentAddress()
    {
        return parentAddress;
    }

    public DatagramSocket getSocket()
    {
        return socket;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }

    public double getLossQuota() {
        return lossQuota;
    }

    public void addMessagesToAll(byte[] data) { //"original" message
        if (!this.isRoot())
        {
            messageQueue.add(new Message(data, parentAddress));
        }
        for (InetSocketAddress childAddress : children)
        {
            messageQueue.add(new Message(data, childAddress));
        }
    }

    public void addMessagesToAll(byte[] data, SocketAddress source) { //non-original message (re-sending)
        if (!this.isRoot() && !source.equals(parentAddress))
        {
            messageQueue.add(new Message(data, parentAddress/*, source*/));
        }
        for (InetSocketAddress childAddress : children)
        {
            if(!source.equals(childAddress)) {
                messageQueue.add(new Message(data, childAddress/*, source*/));
            }
        }
    }

    public Queue<Message> getSentMessages() {
        return sentMessages;
    }

    public void deleteConnection(SocketAddress dest) {
        if (dest.equals(parentAddress))
        {
            isRoot = true;
            System.out.println("Parent " + parentAddress + " doesn't answer, this node is considered a root now");
            parentAddress = null;
        }
        else {
            for (InetSocketAddress child : children)
                if (dest.equals(child)) {
                    children.remove(child);
                    System.out.println("Child " + child + " doesn't answer, won't send messages to it anymore");
                }
            //System.out.println("Child doesn't answer, won't send messages to it anymore");
        }
    }
}
