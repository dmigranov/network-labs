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
            while(true)
            {

                Message msg = node.getMessageQueue().poll();
                if (msg == null)
                    continue;

                if (msg.incrementCount() > Message.maxTryCount)
                {

                }
                node.getMessageQueue().add(msg); //TODO: !!!Раскомментить когда подтверждение доставки!!!

                packet = new DatagramPacket(msg.getData(), msg.getData().length, msg.getDest());
                node.getSocket().send(packet);
                /*if (!node.isRoot() && (msg.isOriginal() || !msg.getSource().equals(node.getParentAddress())))
                {

                    //System.out.println("I'm here");
                    packet = new DatagramPacket(data, data.length, node.getParentAddress());
                    node.getSocket().send(packet); //TODO: Acknowledgement!!!!!
                }

                for (InetSocketAddress childAddress : node.getChildrenAddresses())
                {
                    if(msg.isOriginal() || !msg.getSource().equals(childAddress)) {
                        packet = new DatagramPacket(data, data.length, childAddress);
                        node.getSocket().send(packet); //TODO: Acknowledgement!
                    }
                }*/
                /*if(!msg.isOriginal())
                {
                    data = new byte[17];

                    data[0] = TreeNode.msgAck;
                    System.arraycopy(msg.getUUIDBytes(), 0, data, 1, 16msg.getUUIDBytes().length);
                    packet = new DatagramPacket(data, data.length, msg.getSource());
                    node.getSocket().send(packet); //this is ack!
                }*/
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }
}
