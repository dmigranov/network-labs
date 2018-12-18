package server;

import io.undertow.Undertow;
import server.handlers.RootHandler;

public class Server {
    public static void main(String[] args)
    {
        if(args.length < 1){
            System.err.println("Not enough arguments");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        Undertow server;
        Users users = new Users();
        Messages messages = new Messages();
        Undertow.Builder builder = Undertow.builder().addHttpListener(port, "localhost").
                //setHandler(Handlers.path().addExactPath("/messages_ws", Handlers.websocket(new WebsocketGetMessagesHandler()))).
                setHandler(new RootHandler(users, messages));

        server = builder.build();
        server.start();


    }
}
