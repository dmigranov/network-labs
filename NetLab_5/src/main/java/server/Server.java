package server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import server.handlers.RootHandler;
import server.handlers.WebsocketGetMessagesHandler;

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
                setHandler(Handlers.path().addExactPath("/messages_ws", Handlers.websocket(new WebsocketGetMessagesHandler()))).
                setHandler(new RootHandler(users, messages));

        server = builder.build();
        server.start();

        while(true) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {}

            for (int uid = 0; uid < users.size(); uid++) {
                User user = users.get(uid);
                if(user.incrementOnlineCounter() > 5 && user.isOnline()) {
                    user.setOnline(false);
                    messages.add(new Message(user.getUsername() + " left", -1));
                }
            }
        }
    }
}
