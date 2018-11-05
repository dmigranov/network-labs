package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws IOException
    {
        if(args.length < 1){
            System.err.println("Not enough arguments");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
        server.createContext("/", new RestHandler());
        server.start();
    }
}
