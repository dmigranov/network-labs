import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ChatReader implements Runnable
{
    TreeNode node = null;



    public ChatReader(TreeNode node)
    {
        this.node = node;
    }



    @Override
    public void run() {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1); //change size!!!
        try(DatagramSocket socket = new DatagramSocket(node.getOwnPort()))
        {

            while(true)
            {
                try
                {
                    socket.receive(packet);
                    byte[] data = packet.getData();
                    System.out.println("got it");
                    if (data[0] == 100)
                    {
                        System.out.println("New child connected: " + packet.getAddress() + ":" + packet.getPort());
                        node.addChild(new InetSocketAddress(packet.getAddress(), packet.getPort()));
                    }
                    else if (data[0] == 10) //the first byte's first bit is 0, so UTF-8 sees it as a ASCII character
                    {
                        String str = (new String(data, "UTF-8")).substring(1);
                        System.out.println("From " + packet.getAddress() + ":" + packet.getPort() + ": " + str);
                        //TODO: send to children!

                    }
                }
                catch(IOException e)
                {
                    continue;
                }

            }
        }

        catch (SocketException e)
        {
            System.err.println(e.getMessage());
            System.exit(3);
        }
    }



}