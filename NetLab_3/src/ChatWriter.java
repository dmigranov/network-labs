import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.UUID;

public class ChatWriter// implements Runnable
{
    private TreeNode node = null;

    public ChatWriter(TreeNode node)
    {
        this.node = node;
    }

    public void run() {
        DatagramPacket packet; //TODO: change size? size = 508? 512? 500? fragmentation...

        try
        {
            while(true) {

                Message msg = node.getMessageQueue().poll(); //right now I'm assuming everything is okay! so I don't push it back
                if (msg == null)
                    continue;

                byte[] data = msg.getData();
                if (!node.isRoot()) {
                    //System.out.println("I'm here");
                    packet = new DatagramPacket(data, data.length, node.getParentAddress());
                    node.getSocket().send(packet); //TODO: Acknowledgement!!!!!
                }

                for (InetSocketAddress childAddress : node.getChildrenAddresses()) {
                    packet = new DatagramPacket(data, data.length, childAddress);
                    node.getSocket().send(packet); //TODO: Acknowledgement!
                }
            }

        }
        /*catch(SocketException e)
        {
            e.printStackTrace();
            System.exit(3);
        }*/
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }
}
