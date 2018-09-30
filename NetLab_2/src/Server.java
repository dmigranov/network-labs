import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable
{
    private Socket s;
    private AtomicInteger number;
    private static AtomicInteger threadNumber = new AtomicInteger(0) ;


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
            for(;/*i<50*/; threadNumber.getAndIncrement())
            {
                new Server(server.accept(), threadNumber);
            }
        }
        catch(IOException e)
        {
            System.err.println("Can't receive connections: " + e.getMessage());

        }
    }

    private Server(Socket s, AtomicInteger number)
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
            int count;
            String fileName = socketDataIn.readUTF();


            long fileSize = socketDataIn.readLong();


            File downloaded = new File("uploads/" + fileName);
            downloaded.getParentFile().mkdirs();

            downloaded.createNewFile(); //what if such a file exists already? should I delete it or what?
            FileOutputStream filestream = new FileOutputStream(downloaded);

            //Timer speedTimer = new Timer();

            long periodStart = System.currentTimeMillis(), start = periodStart ;
            int allCount = 0, speedCount = 0;
            byte[] buf = new byte[8192];
            while(allCount < fileSize)
            {
                count = socketIn.read(buf);
                filestream.write(buf, 0, count);
                long now = System.currentTimeMillis(), diff = now - periodStart;
                if(diff >= 3000)
                {
                    System.out.println("Server Thread №" + number + ": Speed right now is " + speedCount/diff + " bytes per second");
                    periodStart = now;
                    speedCount = 0;
                }
                allCount += count;
                speedCount += count;
            }
            long finish = System.currentTimeMillis();
            byte msg = 100;
            socketOut.write(msg);
            filestream.close();
            System.out.println("Server Thread №" + number + ": Successfully downloaded " + fileName + " with average speed " + fileSize/(finish - start) + " bytes per second in " + (finish-start)/1000.0+ " seconds");
            threadNumber.decrementAndGet();
        }
        catch (IOException e)
        {
            System.err.println("Server Thread №" + number + ": Can't get streams: " + e.getMessage());
        }

    }
}
