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
        ChatReader chatReader = null;
        ChatWriter chatWriter = null;

        String nodeName = args[0];
        double lossQuota = Double.parseDouble(args[1]); //0 <= lQ <= 1 ?
        int ownPort = Integer.parseInt(args[2]);
        InetAddress parentIP = null;

        if(args.length == 3) {
            chatWriter = new ChatWriter(nodeName, lossQuota, ownPort);
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
            chatWriter = new ChatWriter(nodeName, lossQuota, ownPort, parentIP, parentPort);
        }
        //new Thread(new ChatReader(node)).start();
        //new ChatWriter(node).run();



    }
}
