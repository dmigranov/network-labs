import java.io.IOException;
import java.net.*;

public class CopySender
{
    private DatagramSocket socket = null;
    private InetAddress groupIP;
    private int port;

    public CopySender(String strGroupIP, String strPort) throws IOException
    {
        socket = new DatagramSocket();
        groupIP = InetAddress.getByName(strGroupIP);
        port = Integer.parseInt(strPort);
    }

    public void run()
    {
        byte[] msg = new byte[1];
        msg[0] = 1;
        DatagramPacket packet = new DatagramPacket(msg, msg.length, groupIP, port);

        while(true) {
            try
            {
                socket.send(packet);
            }
            catch(IOException e)
            {
                System.err.println("Can't send a packet: " + e.getMessage());
                continue;
            }

            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            CopyReceiver.clearSet();
        }
    }
}

