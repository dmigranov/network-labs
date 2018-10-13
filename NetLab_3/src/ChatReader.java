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

                    if (packet.getData()[0] == 100) {
                        System.out.println("New child connected: " + packet.getAddress() + ":" + packet.getPort());
                        node.addChild(new InetSocketAddress(packet.getAddress(), packet.getPort()));
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