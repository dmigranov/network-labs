import java.net.DatagramSocket;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatWriter// implements Runnable
{

    private DatagramSocket socket = null;

    private String nodeName;
    private double lossQuota;
    private int ownPort;
    private InetAddress parentIP = null;
    private int parentPort = 0;
    private List<TreeNode> children = new ArrayList<TreeNode>();


    public void run() {

    }

    public ChatWriter(String nodeName, double lossQuota, int ownPort) //root
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
    }

    public ChatWriter(String nodeName, double lossQuota, int ownPort, InetAddress parentIP, int parentPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
        this.parentIP = parentIP;
        this.parentPort = parentPort;
        notifyParent();


        //parent = null;
    }


    public void notifyParent()
    {

    }
}
