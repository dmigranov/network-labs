import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.UUID;

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
                //block for a time period only???!
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
                    node.getMessageQueue().add(new Message(data, packet.getSocketAddress())); //там и рассылка другим, и отправка подтверждения
                }
                else if (data[0] == TreeNode.msgAck)
                {
                    byte[] uuidBytes = new byte[16];
                    System.arraycopy(data, 1, uuidBytes, 0, 16/*msg.getUUIDBytes().length*/);
                    /*ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    long mostSigBits = bb.getLong();
                    long leastSigBits = bb.getLong();
                    UUID uuid = new UUID(mostSigBits, leastSigBits);
                    System.out.println(uuid);*/
                    for (Message msg: node.getMessageQueue())
                    {
                        if (msg.getUUIDBytes().equals((uuidBytes))) {

                            node.getMessageQueue().remove(msg);
                        }
                    }
                    node.getMessageQueue().add(new Message(data, packet.getSocketAddress()));
                }
            }
            catch(IOException e)
            {
                continue;
            }
        }
    }
}