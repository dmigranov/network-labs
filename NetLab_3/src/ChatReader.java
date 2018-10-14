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



                    System.out.println("New child connected: " + packet.getAddress() + ":" + packet.getPort());
                    node.addChild(new InetSocketAddress(packet.getAddress(), packet.getPort()));
                }
                else if (data[0] == TreeNode.msgByte) //the first byte's first bit is 0, so UTF-8 sees it as a ASCII character
                {
                    String str = (new String(data, "UTF-8")).substring(1);
                    //System.out.println("From " + packet.getAddress() + ":" + packet.getPort() + ": " + str);
                    System.out.println(str);

                    if(!node.isRoot() && !packet.getSocketAddress().equals(node.getParentAddress()))
                    {
                        DatagramPacket sendPacket = new DatagramPacket(data, data.length, node.getParentAddress());
                        node.getSocket().send(sendPacket); //TODO: Acknowledgement!!!!!
                    }

                    for(InetSocketAddress childAddress: node.getChildrenAddresses())
                    {
                        if(!packet.getSocketAddress().equals(childAddress)) {
                            DatagramPacket sendPacket = new DatagramPacket(data, data.length, childAddress);
                            node.getSocket().send(sendPacket); //TODO: Acknowledgement! Возможно, отправку с подтверждением стоило бы вынести в отдельный метод в классе TreeNode
                        }
                    }
                }
            }
            catch(IOException e)
            {
                continue;
            }
        }
    }
}