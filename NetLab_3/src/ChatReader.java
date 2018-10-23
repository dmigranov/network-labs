import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class ChatReader implements Runnable
{
    private TreeNode node;
    private Deque<UUID> receivedMessages = new LinkedList<>(); //or uuidBytes?

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


                if (receivedMessages.size() > 50)
                    clearOld();
                DatagramPacket packet = new DatagramPacket(new byte[512], 512);
                node.getSocket().receive(packet);

                if(random.nextInt(100) < node.getLossQuota()) {
                    //System.out.
                    continue;
                }

                byte[] data = packet.getData();
                if (data[0] == TreeNode.childByte)
                {
                    byte[] msg = new byte[1];
                    msg[0] = TreeNode.childAck;
                    DatagramPacket answer = new DatagramPacket(msg, msg.length, packet.getSocketAddress());
                    node.getSocket().send(answer);
                    if(!node.getChildrenAddresses().contains(packet.getSocketAddress())) {
                        System.out.println("New child connected: " + packet.getAddress() + ":" + packet.getPort());
                        node.addChild(new InetSocketAddress(packet.getAddress(), packet.getPort()));
                    }
                }
                else if (data[0] == TreeNode.msgByte) //the first byte's first bit is 0, so UTF-8 sees it as a ASCII character
                {

                    String str = (new String(data, "UTF-8")).replace("\0", "");
                    UUID uuid = UUID.nameUUIDFromBytes(str.getBytes("UTF-8"));
                    if(!receivedMessages.contains(uuid))
                    {
                        //System.out.print("I'm here: ");
                        System.out.println(str.substring(1));
                        receivedMessages.addFirst(uuid);
                        //node.addMessagesToAll(data, packet.getSocketAddress()); //рассылка другим включая ack
                        node.addMessagesToAll(str.getBytes("UTF-8"), packet.getSocketAddress());
                        //TODO: низлежащий код заменить на вызов node.addAckMessage (чтобы ack переотправлялся)
                        data = new byte[17];
                        byte[] uuidBytes = new byte[16];
                        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                        bb.putLong(uuid.getMostSignificantBits());
                        bb.putLong(uuid.getLeastSignificantBits());
                        data[0] = TreeNode.msgAck;
                        System.arraycopy(uuidBytes, 0, data, 1, 16);
                        packet = new DatagramPacket(data, data.length, packet.getSocketAddress());
                        node.getSocket().send(packet);
                    }

                    //System.out.println("Child sent to father UUID: " + uuid);
                    //this  re-sends acks until message is retieved from deque. that's bad
                    /*data = new byte[17];
                    byte[] uuidBytes = new byte[16];
                    ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    bb.putLong(uuid.getMostSignificantBits());
                    bb.putLong(uuid.getLeastSignificantBits());
                    data[0] = TreeNode.msgAck;
                    System.arraycopy(uuidBytes, 0, data, 1, 16);
                    packet = new DatagramPacket(data, data.length, packet.getSocketAddress());
                    node.getSocket().send(packet);*/

                }
                else if (data[0] == TreeNode.msgAck)
                {
                    //когда мы посылаем от одного одинаковые сообщения, у них один текст! поэтому по доставке одного удаляются оба
                    byte[] uuidBytes = new byte[16];
                    System.arraycopy(data, 1, uuidBytes, 0, 16/*msg.getUUIDBytes().length*/);
                    ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    long mostSigBits = bb.getLong();
                    long leastSigBits = bb.getLong();
                    UUID uuid = new UUID(mostSigBits, leastSigBits);
                    //System.out.println("father got UUID back: " + uuid);

                    //for (Message msg : node.getMessageQueue()) {
                    for (Message msg : node.getSentMessages()) {
                        if (msg.getUUID().equals((uuid)) && msg.getDest().equals(packet.getSocketAddress())) {
                            //System.out.println("Deleted");
                            node.getSentMessages().remove(msg);
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

    private void clearOld() {
        while(receivedMessages.size() > 50)
        {
            receivedMessages.pollLast();
            System.out.println("cleared old");
        }
    }
}