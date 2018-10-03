

import java.io.*;
import java.net.*;
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


        int port = Integer.parseInt(args[0]);
        try(ServerSocket server = new ServerSocket(port)) { //auto-closeable
            System.out.println("The server started working");
            for(;; threadNumber.incrementAndGet())
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
        //t.setDaemon(true);
        t.start();
    }


    @Override
    public void run()
    {
        File downloaded = null;
        FileOutputStream fileStream = null;
        try(OutputStream socketOut = s.getOutputStream();
            InputStream socketIn = s.getInputStream();
            DataOutputStream socketDataOut = new DataOutputStream(socketOut);
            DataInputStream socketDataIn = new DataInputStream(socketIn))
        {
            int count;
            String fileName = socketDataIn.readUTF();


            long fileSize = socketDataIn.readLong();


            downloaded = new File("uploads/" + fileName);
            downloaded.getParentFile().mkdirs();

            downloaded.createNewFile(); //what if such a file exists already? should I delete it or what?
            fileStream = new FileOutputStream(downloaded, false);


            long periodStart = System.currentTimeMillis(), start = periodStart ;
            long allCount = 0, speedCount = 0;
            byte[] buf = new byte[8192];
            //s.setSoTimeout(3000);
            while(allCount < fileSize)
            {


                //try {
                count = socketIn.read(buf);
                /*}
                catch(SocketTimeoutException ste)/{
                    continue;
                }*/
                fileStream.write(buf, 0, count);

                allCount += count;
                speedCount += count;
                long now = System.currentTimeMillis(), diff = now - periodStart;
                if(diff >= 3000)
                {
                    System.out.println("Server Thread №" + number + ": Speed right now is " + 1000*speedCount/diff + " bytes per second (" + speedCount + " bytes)");
                    periodStart = now;
                    speedCount = 0;
                }

            }
            long finish = System.currentTimeMillis();
            byte msg = 100;
            socketOut.write(msg);
            fileStream.close();
            System.out.println("Server Thread №" + number + ": Successfully downloaded " + fileName + " with average speed " + 1000*fileSize/(finish - start) + " bytes per second in " + (finish-start)/1000.0+ " seconds");

        }
        catch (SocketException e)
        {
            //System.err.println("Server Thread №" + number + ": Socket error: " + e.getMessage());
            if(fileStream!= null)
                try {
                    fileStream.close();
                    if(downloaded.exists())
                        downloaded.delete();
                }
                catch (IOException e2)
                {
                    System.err.println("Server Thread №" + number + ": Can't close file output stream: " + e2.getMessage());
                }

            System.err.println("Server Thread №" + number + ": Socket error: " + e.getMessage());
        }
        catch (IOException e)
        {
            System.err.println("Server Thread №" + number + ": Connection error: " + e.getMessage());
        }
        finally
        {
            threadNumber.decrementAndGet();
        }

    }
}
