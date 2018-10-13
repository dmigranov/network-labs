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
                data = str.getBytes("UTF-8");
                if(!node.isRoot())
                {
                    //send to parent
                }

                //send to children

            }

        }
        catch(SocketException e)
        {

            System.err.println(e.getMessage());
            System.exit(3);
        }
        catch(IOException e)
        {
            System.err.println("Can't create a System.in reader!");
            System.exit(5);
        }

    }


}
