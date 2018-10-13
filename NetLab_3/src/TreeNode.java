import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class TreeNode
{
    //private TreeNode parent = null;
    private String nodeName;
    private double lossQuota;
    private int ownPort;
    private InetAddress parentIP = null;
    private int parentPort = 0;
    private List<TreeNode> children = new ArrayList<TreeNode>();


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
        this.parentIP = parentIP;
        this.parentPort = parentPort;
        notifyParent();


        //parent = null;
    }
    public void notifyParent()
    {

    }

    public void addChild()
    {

    }
}
