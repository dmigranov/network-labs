import java.net.InetAddress;

public class TreeNode
{
    private TreeNode parent;
    private String nodeName;
    private double lossQuota;
    private int ownPort;

    public TreeNode(String nodeName, double lossQuota, int ownPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
        parent = null;
    }

    public TreeNode(String nodeName, double lossQuota, int ownPort, InetAddress parentIP, int parentPort)
    {
        this.nodeName = nodeName;
        this.lossQuota = lossQuota;
        this.ownPort = ownPort;
        parent = null;
    }
}
