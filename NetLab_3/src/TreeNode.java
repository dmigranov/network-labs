import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TreeNode
{
    //private TreeNode parent = null;

    private DatagramSocket socket = null; //shall be closed in CR or CW

    private String nodeName;
    private double lossQuota;
    private int ownPort;
    //private InetAddress parentIP = null;
    private InetSocketAddress parentAddress = null;
    //private int parentPort = 0;
    //private List<InetSocketAddress> children = new ArrayList<>();
    private List<InetSocketAddress> children = new CopyOnWriteArrayList<>(); //is it needed???
    private boolean isRoot = true;


    public TreeNode(String nodeName, double lossQuota, int ownPort)
    {
        this.nodeName = nodeName;
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

    public TreeNode(String nodeName, double lossQuota, int ownPort, InetAddress parentIP, int parentPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
        this.parentAddress = new InetSocketAddress(parentIP, parentPort);
        //this.parentPort = parentPort;
        isRoot = false;

        try
        {
            socket = new DatagramSocket(ownPort);
        }
        catch(SocketException e)
        {
            System.err.println(e.getMessage());
            System.exit(3);
        }

        notifyParent();


        //parent = null;
    }
    private void notifyParent()
    {
        //TODO: acknowledgement needed!!!!!


        byte[] msg = new byte[1];
        msg[0] = 100;
        DatagramPacket packet = new DatagramPacket(msg, msg.length, parentAddress);
        try {
            socket.send(packet);

        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
            System.exit(4);
        }
    }

    public void addChild(InetSocketAddress childAddress)
    {
        children.add(childAddress);
        //System.out.println("added a child!");
    }

    public List<InetSocketAddress> getChildrenAddresses()
    {
        return children;
    }

    public int getOwnPort()
    {
        return ownPort;
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
}