
import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class Client {
    public static void main(String[] args)
    {
        if(args.length < 3) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        String path = args[0];
        int port = Integer.parseInt(args[2]);

        InetAddress serverAddr = null;
        try {
            serverAddr = InetAddress.getByName(args[1]);
        }
        catch(UnknownHostException e)
        {
            System.err.println("Can't recognize host: " + e.getMessage());
            System.exit(2);
        }

        File file = new File(path);

        try (Socket s = new Socket(serverAddr, port);
             FileInputStream filestream = new FileInputStream(file);
             OutputStream socketOut = s.getOutputStream();
             DataOutputStream socketDataOut = new DataOutputStream(socketOut);
             InputStream socketIn = s.getInputStream();
             DataInputStream socketDataIn = new DataInputStream(socketIn))
        {
            //передача имени (UTF-8)
            String fileName = file.getName(); //<= 4096 bytes - server should have such a buffer!
            socketDataOut.writeUTF(fileName);

            //передача размера файла
            socketDataOut.writeLong(file.length());

            //передача самого файла
            byte[] buf = new byte[8192];
            int count;
            while((count = filestream.read(buf)) > 0)
            {
                socketOut.write(buf, 0, count);
            } //sending - done


            int msg = socketIn.read();
            if(msg == 100)
                System.out.println("Succesfully sent " + fileName);

        }
        catch(FileNotFoundException e)
        {
            System.err.println("Can't find the file: " + e.getMessage());
            System.exit(3);
        }
        catch(IOException e)
        {
            System.err.println("Can't connect to the server: " + e.getMessage());
            System.exit(4);
        }

    }
}
