import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class ChatReader implements Runnable
{
    private final TreeNode node;
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
                    String textStr = (new String(data, "UTF-8").substring(9)).replace("\0", "");
                    byte[] strBytes = textStr.getBytes("UTF-8");
                    byte[] newData = new byte[9 + strBytes.length];
                    System.arraycopy(data, 0, newData, 0, 9);
                    System.arraycopy(strBytes, 0, newData, 9, strBytes.length);
                    data = newData;
                    UUID uuid = UUID.nameUUIDFromBytes(data);
                    if(!receivedMessages.contains(uuid))
                    {
                        byte[] millisBytes = new byte[8];
                        System.arraycopy(data, 1, millisBytes, 0, 8);
                        ByteBuffer bb = ByteBuffer.wrap(millisBytes);
                        long dateMillis = bb.getLong();
                        //Date date = new Date(dateMillis);
                        GregorianCalendar cal = new GregorianCalendar();
                        cal.setTimeInMillis(dateMillis);
                        String formattedTime = String.format("%02d.%02d %02d:%02d:%02d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
                        //System.out.println(cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +  "   " + str2);
                        System.out.println(formattedTime + "   " + textStr);
                        receivedMessages.addFirst(uuid);
                        node.addMessagesToAll(data, packet.getSocketAddress());
                    }

                    //System.out.println("Child sent to father UUID: " + uuid);
                    //ack надо отправить в любом случае - и если сообщение пришло в первый раз, и если перепришло!
                    data = new byte[17];
                    byte[] uuidBytes = new byte[16];
                    ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                    bb.putLong(uuid.getMostSignificantBits());
                    bb.putLong(uuid.getLeastSignificantBits());
                    data[0] = TreeNode.msgAck;
                    System.arraycopy(uuidBytes, 0, data, 1, 16);
                    packet = new DatagramPacket(data, data.length, packet.getSocketAddress());
                    for (int i = 0; i < 5; i++)
                        node.getSocket().send(packet);
                }
                else if (data[0] == TreeNode.msgAck)
                {
                    synchronized(node) {
                        byte[] uuidBytes = new byte[16];
                        System.arraycopy(data, 1, uuidBytes, 0, 16/*msg.getUUIDBytes().length*/);
                        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
                        long mostSigBits = bb.getLong();
                        long leastSigBits = bb.getLong();
                        UUID uuid = new UUID(mostSigBits, leastSigBits);
                        //System.out.println("father got UUID back: " + uuid);
                        for (Message msg : node.getSentMessages()) {
                            if (msg.getUUID().equals((uuid)) && msg.getDest().equals(packet.getSocketAddress())) {
                                //System.out.println("Deleted");
                                node.getSentMessages().remove(msg);
                            }
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