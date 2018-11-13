package server;

import io.undertow.Undertow;

public class Server {
    public static void main(String[] args)
    {
        if(args.length < 1){
            System.err.println("Not enough arguments");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        Undertow server;
        Undertow.Builder builder = Undertow.builder().addHttpListener(port, "localhost").setHandler(new RestHandler());

        server = builder.build();
        server.start();

        //TODO: cleaner
        //while true sleep counter++ isOnline = false

    }
}
