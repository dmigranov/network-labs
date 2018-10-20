import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ChatReader implements Runnable
{
    private TreeNode node = null;

    public ChatReader(TreeNode node)
    {
        this.node = node;
    }

    @Override
    public void run() {
        while(true)
        {
            try
            {
                DatagramPacket packet = new DatagramPacket(new byte[512], 512);
                node.getSocket().receive(packet);
                byte[] data = packet.getData();
                if (data[0] == TreeNode.childByte)
                {
                    byte[] msg = new byte[1];
                    msg[0] = TreeNode.childAck;
                    DatagramPacket answer = new DatagramPacket(msg, msg.length, packet.getSocketAddress());
                    node.getSocket().send(answer);

                    System.out.println("New child connected: " + packet.getAddress() + ":" + packet.getPort());
                    node.addChild(new InetSocketAddress(packet.getAddress(), packet.getPort()));
                }
                else if (data[0] == TreeNode.msgByte) //the first byte's first bit is 0, so UTF-8 sees it as a ASCII character
                {
                    String str = (new String(data, "UTF-8")).substring(1);
                    System.out.println(str);
                    node.getMessageQueue().add(new Message(data, packet.getSocketAddress()));


                    /*if(!node.isRoot() && !packet.getSocketAddress().equals(node.getParentAddress()))
                    {
                        DatagramPacket sendPacket = new DatagramPacket(data, data.length, node.getParentAddress());
                        node.getSocket().send(sendPacket); //TODO: Acknowledgement!!!!!
                    }

                    for(InetSocketAddress childAddress: node.getChildrenAddresses())
                    {
                        if(!packet.getSocketAddress().equals(childAddress)) {
                            DatagramPacket sendPacket = new DatagramPacket(data, data.length, childAddress);
                            node.getSocket().send(sendPacket); //TODO: Acknowledgement! Возможно, чтобы обеспечить отсутствие задержек, стоит создать очередь сообщений и отправлять по очереди. Очередь ограниченная, выкидывать по какой-то системе
                        }
                    }*/
                }
            }
            catch(IOException e)
            {
                continue;
            }
        }
    }
}