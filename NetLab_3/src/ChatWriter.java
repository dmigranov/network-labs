import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.BufferedReader;

public class ChatWriter// implements Runnable
{

    TreeNode node = null;

    public ChatWriter(TreeNode node)
    {
        this.node = node;
    }




    public void run() {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1); //change size!!!
        while(true)
        {
            try
            {
                node.getSocket().receive(packet);
                System.out.println(packet.getData());


            }
            catch (IOException e)
            {
                continue;
            }
        }
    }


}
