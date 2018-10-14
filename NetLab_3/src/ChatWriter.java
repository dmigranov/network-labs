import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.io.BufferedReader;
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
        byte [] data;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            String str;
            while((str = br.readLine()) != null)
            {
                String resStr = node.getNodeName() + ": " + str;
                byte[] strBytes = resStr.getBytes("UTF-8");
                UUID uuid = UUID.nameUUIDFromBytes(strBytes); //16 bytes. I guess there is no need to send it - it can easily be recalculated!
                //добавить в очередь!


                data = new byte[strBytes.length + 1];
                data[0] = TreeNode.msgByte;

                System.arraycopy(strBytes, 0, data, 1, strBytes.length);
                if(!node.isRoot())
                {
                    //System.out.println("I'm here");
                    packet = new DatagramPacket(data, data.length, node.getParentAddress());
                    //try //catch inside of while?
                    node.getSocket().send(packet); //TODO: Acknowledgement!!!!!
                }

               for(InetSocketAddress childAddress: node.getChildrenAddresses())
               {
                   packet = new DatagramPacket(data, data.length, childAddress);
                   node.getSocket().send(packet); //TODO: Acknowledgement!
               }
            }
        }
        catch(SocketException e)
        {
            e.printStackTrace();
            System.exit(3);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }
}
