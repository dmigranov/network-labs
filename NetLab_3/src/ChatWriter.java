import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.BufferedReader;
import java.net.InetSocketAddress;

public class ChatWriter// implements Runnable
{

    private TreeNode node = null;


    public ChatWriter(TreeNode node)
    {
        this.node = node;
    }




    public void run() {
        //DatagramPacket packet = new DatagramPacket(new byte[1], 1); //change size!!!
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            String str;
            while((str = br.readLine()) != null)
            {
                System.out.println(str);
            }

        }
        catch(IOException e)
        {
            System.err.println("Can't create a System.in reader!");
            System.exit(5);
        }

    }


}
