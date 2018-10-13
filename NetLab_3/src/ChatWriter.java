import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.net.SocketException;

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
                //System.out.println(str);
                byte[] strBytes = str.getBytes("UTF-8");
                data = new byte[strBytes.length + 1];
                data[0] = 10;

                //TODO: node name, don't forget about it!!!

                System.arraycopy(strBytes, 0, data, 1, strBytes.length);
                if(!node.isRoot())
                {
                    //System.out.println("I'm here");
                    packet = new DatagramPacket(data, data.length, node.getParentAddress());

                    //try //catch inside of while?
                    node.getSocket().send(packet); //TODO: Acknowledgement!!!!!
                }
               //send to children

            }

        }
        catch(SocketException e)
        {

            //System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
        catch(IOException e)
        {
            //System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(5);
        }

    }


}
