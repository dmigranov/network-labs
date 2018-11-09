package client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {

    public static void main(String[] args) {
        if(args.length < 2){ //на вход - URL и ник
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        String username = args[1];

        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            //login:
            URL url = new URL(args[0] + "/login");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);

            con.setRequestMethod("POST");
            con.setRequestProperty("Host", "localhost");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStream os = con.getOutputStream();

            byte data[] = ("{ \"username\": \"" + username +" \" }").getBytes("UTF-8"); //maybe build json with a special method
            os.write(data);

            //InputStream is = con.getInputStream();
            System.out.println(con.getResponseCode());
            System.out.println(con.getHeaderField("Content-Type"));
            System.out.println(con.getHeaderField("WWW-Authenticate")); //TODO: сделать все необходимые проверки
            //получить тело


            String str;
            while((str = br.readLine()) != null)
            {
                //con = null;
                //while ... read line ... в зависимости от линии url + "..." ;
                //con.getRe
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }
}
