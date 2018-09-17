import java.io.IOException;
import java.net.*;
import java.util.*;


public class CopyReceiver implements Runnable
{
    private MulticastSocket socket = null;
    private static Set<String> actualUsers = new TreeSet<String>(), oldUsers = new TreeSet<String>();


    public CopyReceiver(String strGroupIP, String strPort) throws IOException
    {
        InetAddress groupIP = InetAddress.getByName(strGroupIP);
        int port = Integer.parseInt(strPort);

        socket = new MulticastSocket(port);

        socket.joinGroup(groupIP);
    }

    @Override
    public void run() {
        while(true)
        {
            DatagramPacket packet = new DatagramPacket(new byte[1], 1);
            try
            {
                socket.receive(packet);
            }
            catch(IOException e)
            {
                continue;
            }
            actualUsers.add(packet.getAddress().toString() + ":" + packet.getPort());
        }
    }

    public static void clearSet()
    {
        if(!actualUsers.equals(oldUsers))
        {
            for(String s : actualUsers)
            {
                System.out.println(s);
            }
            System.out.println();
        }
        oldUsers = new TreeSet<String>(actualUsers);
        actualUsers.clear();
    }

}
