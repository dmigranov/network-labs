import java.io.*;
import java.net.*;

public class Server implements Runnable
{
    private Socket s;
    private int number;


    public static void main(String[] args)
    {
        if(args.length < 1) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }


       // ServerSocket server;
        int port = Integer.parseInt(args[0]);
        try(ServerSocket server = new ServerSocket(port)) { //auto-closeable
             //IP localhost? //backlog = 50
            System.out.println("The server started working");
            for(int i = 0 ;/*i<50*/; i++)
            {
                new Server(server.accept(), i);
            }
        }
        catch(IOException e)
        {
            System.err.println("Can't receive connections: " + e.getMessage());

        }
    }

    private Server(Socket s, int number)
    {
        this.s = s;
        this.number = number;

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }


    @Override
    public void run()
    {
        try(OutputStream socketOut = s.getOutputStream();
            InputStream socketIn = s.getInputStream();
            DataOutputStream socketDataOut = new DataOutputStream(socketOut);
            DataInputStream socketDataIn = new DataInputStream(socketIn))
        {
            //byte[] buf = new byte[4096];
            int count;
            String fileName = socketDataIn.readUTF();
            /*while((count = socketIn.read(buf)) != -1)
            {

                //System.out.print(new String(buf, "UTF-8"));
                fileName += new String(buf, "UTF-8");
                System.out.println(count);
                //System.out.println();
            }*/

            //socketOut.write(100);
            System.out.println("File name: " + fileName);

            long fileSize = socketDataIn.readLong();
            System.out.println("File size: " + fileSize);

            File downloaded = new File("uploads/" + fileName);
            downloaded.getParentFile().mkdirs();

            downloaded.createNewFile(); //what if such a file exists already? should I delete it or what?
            FileOutputStream filestream = new FileOutputStream(downloaded);

            byte[] buf = new byte[8192];
            while(fileSize > 0)
            {
                count = socketIn.read(buf);
                filestream.write(buf, 0, count);
                fileSize -= count;
            }
            byte msg = 100;
            socketOut.write(msg);
        }
        catch (IOException e)
        {
            System.err.println("Can't get streams: " + e.getMessage());
        }



    }
}
