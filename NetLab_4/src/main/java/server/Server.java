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
        //List<User> users = new CopyOnWriteArrayList<>();
        Users users = new Users(); //TODO
        Messages messages = new Messages();
        Undertow.Builder builder = Undertow.builder().addHttpListener(port, "localhost").setHandler(new CoreHandler(users, messages));

        server = builder.build();
        server.start();

        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

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
