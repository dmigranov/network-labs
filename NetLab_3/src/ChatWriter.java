import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.BufferedReader;

public class ChatWriter// implements Runnable
{

    private TreeNode node = null;


    public ChatWriter(TreeNode node)
    {
        this.node = node;
    }




    public void run() {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1); //change size!!!
        while(true)
        {
            try(DatagramSocket socket = new DatagramSocket(node.getOwnPort()))
            {
                socket.receive(packet);
                System.out.println("New child connected: " + packet.getAddress() + ":" + packet.getPort());


            }
            catch (IOException e)
            {
                continue;
            }
        }
    }


}
