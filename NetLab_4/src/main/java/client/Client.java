package client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class Client {

    public static void main(String[] args) {
        if(args.length < 2){ //на вход - URL и ник
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        String username = args[1];
        String token;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            URL url = new URL(args[0] + "/login");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Host", "localhost");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStream os = con.getOutputStream();
            byte data[] = ("{ \"username\": \"" + username +" \" }").getBytes("UTF-8"); //maybe build json with a special method
            os.write(data);
            System.out.println(con.getResponseCode());
            if(con.getHeaderField("WWW-Authenticate") != null) {
                System.out.println(con.getHeaderField("WWW-Authenticate"));
                System.exit(2);
            }
            InputStream is = con.getInputStream();
            String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //мож по другому
            JSONObject loginInfo = new JSONObject(body);
            token = loginInfo.getString("token");
            con.disconnect(); //is it necessary?
            System.out.println("Connected with username " + username);

            String str;
            while((str = br.readLine()) != null)
            {
                con = (HttpURLConnection)url.openConnection();
                //con.getRe
                if(str.charAt(0) == '/')
                {
                    //служебное сообщение
                    //if...
                }
                else
                {
                    //POST message
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }
}
