import java.net.InetAddress;

public class TreeNode
{
    private TreeNode parent = null;
    private String nodeName;
    private double lossQuota;
    private int ownPort;
    private InetAddress parentIP = null;
    int parentPort = 0;


    public TreeNode(String nodeName, double lossQuota, int ownPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;

    }

    public TreeNode(String nodeName, double lossQuota, int ownPort, InetAddress parentIP, int parentPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
        //parent = null;
    }
}
