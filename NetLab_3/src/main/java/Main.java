import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args)
    {
        if(args.length < 3)
        {
            System.err.println("Not enough arguments");
            System.exit(1);
        }
        TreeNode node = null;

        String nodeName = args[0];
        int lossQuota = Integer.parseInt(args[1]); //0 <= lQ <= 1 ?
        int ownPort = Integer.parseInt(args[2]);
        InetAddress parentIP = null;

        if(args.length == 3) {
            node = new TreeNode(nodeName, lossQuota, ownPort);
        }
        else if (args.length == 5)
        {
            try {
                parentIP = InetAddress.getByName(args[3]);
            }
            catch (UnknownHostException e)
            {
                System.err.println("Can't parse the IP address");
                System.exit(2);
            }
            int parentPort = Integer.parseInt(args[4]);
            node = new TreeNode(nodeName, lossQuota, ownPort, parentIP, parentPort);
        }
        new Thread(new ChatReader(node)).start();
        new Thread(new TerminalReader(node)).start();
        new Thread(new ChatResender(node)).start();
        new ChatWriter(node).run();

    }
}
