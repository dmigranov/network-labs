package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

public class Client {
    public static void main(String[] args) {
        if(args.length < 1){ //на вход - URL
            System.err.println("Not enough arguments");
            System.exit(1);
        }
        HttpURLConnection con = null;
        //на самом деле читать из cmd
        try {
            URL url = new URL(args[0] + "/login");
            con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);

            con.setRequestMethod("POST");
            con.setRequestProperty("Host", "localhost");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStream os = con.getOutputStream();
            byte data[] = ("{ \"username\": \"Чувак\" }").getBytes("UTF-8");
            os.write(data);

            System.out.println(con.getResponseCode());
            //con.getRe

        }
        catch(IOException e)
        {
            System.err.println("Can't open connection: " + e.getMessage()); //
            System.exit(2);
        }


    }
}
