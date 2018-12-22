package proxy;


public class Main {

    public static void main(String[] args) {
        if(args.length == 0)
        {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        new SOCKSProxyServer(port).run();

    }
}
