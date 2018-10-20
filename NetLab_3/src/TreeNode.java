import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class TreeNode
{
private DatagramSocket socket = null; //shall be closed in CR or CW

    private String nodeName;
    private double lossQuota;
    private int ownPort;
    private InetSocketAddress parentAddress = null;
    //private int parentPort = 0;
    //private List<InetSocketAddress> children = new ArrayList<>();
    private Set<InetSocketAddress> children = new CopyOnWriteArraySet<>();
    //к вопросу о лишних записях в сете (мёртвых душах): просто удалять при проверке того, дошло ли сообщение, их из сета
    //впрочем, тогда уже можно будет заменить сет на лист обратно
    //TODO: добавить очередь (список?) сообщений. Сообщение: ID, текст (? а зачем тогда ID?), количество попыток отправки (?)...
    private boolean isRoot = true;
    private Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    //private List<Message> messageList = new CopyOnWriteArrayList<>();

    public final static byte childByte = 100;
    public final static byte msgByte = 10;
    public final static byte childAck = 101;


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
        this(nodeName, lossQuota, ownPort);
        isRoot = false;
        parentAddress = new InetSocketAddress(parentIP, parentPort);

        notifyParent();

        //parent = null;
    }
    private void notifyParent()
    {


        byte[] msg = new byte[1];
        msg[0] = childByte;
        DatagramPacket packet = new DatagramPacket(msg, msg.length, parentAddress);

        try {
            socket.setSoTimeout(5000);

            socket.send(packet);
            DatagramPacket answer = new DatagramPacket(new byte[1], 1);
            socket.receive(answer);
            if (answer.getData()[0] != childAck) //is this system good???
            {
                System.out.println("Can't connect to a parent! The node is considered a root now");
                isRoot = true;
            }

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
    }

    public Set<InetSocketAddress> getChildrenAddresses()
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

    public String getNodeName() {
        return nodeName;
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }

    public double getLossQuota() {
        return lossQuota;
    }
}
