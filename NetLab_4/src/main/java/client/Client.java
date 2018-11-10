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
        //Runtime.getRuntime().addShutdownHook(); //on exit - logout?

        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            URL url = new URL(args[0] + "/login");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Host", "localhost");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStream os = con.getOutputStream();
            byte data[] = new JSONObject().put("username", username).toString().getBytes(StandardCharsets.UTF_8);
            os.write(data);
            //System.out.println(con.getResponseCode());
            if(con.getHeaderField("WWW-Authenticate") != null) {
                System.out.println(con.getHeaderField("WWW-Authenticate"));
                System.exit(2);
            }
            InputStream is = con.getInputStream();
            String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //мож по другому
            JSONObject loginInfo = new JSONObject(body);
            token = loginInfo.getString("token");
            os.close();
            is.close();
            con.disconnect();
            //get messages
            System.out.println("Connected with username " + username);



            String str;
            while((str = br.readLine()) != null)
            {
                if(str.charAt(0) == '/')
                {
                    if (str.equals("/logout"))
                    {
                        url = new URL(args[0] + "/logout");
                        con = (HttpURLConnection)url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Host", "localhost");
                        con.setRequestProperty("Authorization", "Token " + token);
                        is = con.getInputStream();
                        System.out.println(new JSONObject(getStringFromStream(is)).get("message"));
                        System.exit(0);
                    }
                    else if(str.equals("/list")) {
                        url = new URL(args[0] + "/users");
                        con = (HttpURLConnection)url.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Authorization", "Token " + token);
                        con.setRequestProperty("Host", "localhost");
                        is = con.getInputStream();
                    }

                    else
                    {
                        System.out.println("No such command");
                        break;
                    }
                }
                else
                {
                    //post a message
                    url = new URL(args[0] + "/messages");
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Authorization", "Token " + token);
                    con.setRequestProperty("Host", "localhost");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);

                    os = con.getOutputStream();
                    data = new JSONObject().put("message", str).toString().getBytes(StandardCharsets.UTF_8);
                    os.write(data);

                    //System.out.println(con.getResponseCode());
                    is = con.getInputStream();
                    System.out.println(new JSONObject(getStringFromStream(is)).get("id"));
                    //ответ получен

                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }

    static private String getStringFromStream(InputStream is)
    {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
    }
}
