import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TreeNode
{
    //private TreeNode parent = null;

    private DatagramSocket socket = null;

    private String nodeName;
    private double lossQuota;
    private int ownPort;
    private InetAddress parentIP = null;
    private int parentPort = 0;
    private List<TreeNode> children = new ArrayList<TreeNode>();
    private boolean isRoot = true;


    public TreeNode(String nodeName, double lossQuota, int ownPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
        try(DatagramSocket socket = new DatagramSocket(ownPort))
        {
            this.socket = socket;
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
        this.parentIP = parentIP;
        this.parentPort = parentPort;
        isRoot = false;

        try(DatagramSocket socket = new DatagramSocket(ownPort))
        {
            this.socket = socket;
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
        //acknowledgement needed!


        byte[] msg = new byte[1];
        msg[0] = 1;
        DatagramPacket packet = new DatagramPacket(msg, msg.length, parentIP, parentPort);
        try {
            socket.send(packet);
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
            System.exit(4);
        }
    }

    public void addChild()
    {


    }

    public DatagramSocket getSocket()
    {
        return socket;
    }


    public boolean isRoot()
    {
        return isRoot;
    }
}

