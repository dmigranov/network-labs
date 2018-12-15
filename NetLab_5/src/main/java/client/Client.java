package client;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.websocket.DeploymentException;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Client {

    public static void main(String[] args) {
        if(args.length < 2){ //на вход - URL и ник
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        String username = args[1];
        String token = null;
        int uid = 0;
        if(args.length > 3) {
            token = args[2];
            uid = Integer.parseInt(args[3]);
        }
        Users users = new Users();
        //Runtime.getRuntime().addShutdownHook(); //on exit - logout?

        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            URL url;
            HttpURLConnection con;
            OutputStream os;
            InputStream is;
            byte data[];
            if(token == null) {
                url = new URL(args[0] + "/login");
                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Host", "localhost");
                con.setRequestProperty("Content-Type", "application/json");

                os = con.getOutputStream();
                data = new JSONObject().put("username", username).toString().getBytes(StandardCharsets.UTF_8);
                os.write(data);
                if (con.getHeaderField("WWW-Authenticate") != null) {
                    System.out.println(con.getHeaderField("WWW-Authenticate"));
                    System.exit(2);
                }

                is = con.getInputStream();
                String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //мож по другому
                JSONObject loginInfo = new JSONObject(body);
                token = loginInfo.getString("token");
                uid = loginInfo.getInt("id");
                os.close();
                is.close();
                con.disconnect();
                System.out.println("Your token and id are: " + token + " and " + uid + ". Please remember them to be able to re-login");
            }
            new Thread(new MessageRefresher(args[0], token, uid, users)).start();
            new Thread(new WebsocketClient(args[0], token, uid, users)).start();
            String str;
            while((str = br.readLine()) != null)
            {
                if(!str.equals("") && str.charAt(0) == '/')
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
                        JSONArray usersArray = new JSONObject(getStringFromStream(is)).getJSONArray("users");
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject msg = usersArray.getJSONObject(i);
                            System.out.println(msg.get("username") + " - "+ (msg.getBoolean("online") == true ? "online" : "offline"));
                        }
                        is.close();
                    }
                    else
                    {
                        System.err.println("No such command");
                        //break;
                    }
                }
                else //post a message
                {
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

                    is = con.getInputStream();
                    new JSONObject(getStringFromStream(is)).get("id"); //а что выводить? надо ли?
                    is.close();
                    os.close();

                }
            }
        }
        catch(ConnectException d)
        {
            System.err.println("Connection error: the server is dead");
            System.exit(5);
        }
        catch(URISyntaxException | DeploymentException c)
        {
            c.printStackTrace();
        }
        catch(IOException e)
        {
            System.err.println("Error");
            System.exit(5);
        }

    }

    static private String getStringFromStream(InputStream is)
    {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
    }
}
