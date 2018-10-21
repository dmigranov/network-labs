import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

public class ChatReader implements Runnable
{
    private TreeNode node = null;

    public ChatReader(TreeNode node)
    {
        this.node = node;
    }
    Random random = new Random();

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
                    if(random.nextInt(100) < node.getLossQuota()) {
                        //System.out.println("Lost a packet");
                        continue;
                    }
                    String str = (new String(data, "UTF-8")).replace("\0", "");

                    System.out.println(str.substring(1));
                    //node.getMessageQueue().add(new Message(data, packet.getSocketAddress())); //там рассылка другим
                    node.addMessagesToAll(data, packet.getSocketAddress()); //рассылка другим включая ack

                    //UUID uuid = UUID.nameUUIDFromBytes(new String(data, "UTF-8").getBytes("UTF-8"));
                    UUID uuid = UUID.nameUUIDFromBytes(str.getBytes("UTF-8"));
                    //System.out.println("Child sent to father UUID: " + uuid);
                    data = new byte[17];
                    byte[] uuidBytes = new byte[16];
                    //System.out.println("Sent: " + uuid);
                    ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    bb.putLong(uuid.getMostSignificantBits());
                    bb.putLong(uuid.getLeastSignificantBits());
                    data[0] = TreeNode.msgAck;
                    System.arraycopy(uuidBytes, 0, data, 1, 16);
                    packet = new DatagramPacket(data, data.length, packet.getSocketAddress());
                    node.getSocket().send(packet);

                }
                else if (data[0] == TreeNode.msgAck)
                {
                    //
                    byte[] uuidBytes = new byte[16];
                    System.arraycopy(data, 1, uuidBytes, 0, 16/*msg.getUUIDBytes().length*/);
                    ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    long mostSigBits = bb.getLong();
                    long leastSigBits = bb.getLong();
                    UUID uuid = new UUID(mostSigBits, leastSigBits);
                    //System.out.println("father got UUID back: " + uuid);

                    //for (Message msg : node.getMessageQueue()) {
                    for (Message msg : node.getSentMessages()) {
                        if (msg.getUUID().equals((uuid))) {
                            //System.out.println("Deleted");
                            node.getSentMessages().remove(msg);
                        }

                    }

                    //node.addMessagesToAll(data, packet.getSocketAddress());
                }
            }
            catch(IOException e)
            {
                continue;
            }
        }
    }
}