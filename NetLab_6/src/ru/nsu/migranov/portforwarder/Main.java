package ru.nsu.migranov.portforwarder;

import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        if(args.length < 3)
        {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        int lport = Integer.parseInt(args[0]);
        InetAddress rhost = null;
        try
        {
            rhost = InetAddress.getByName(args[1]);
        }
        catch(java.net.UnknownHostException e)
        {
            System.err.println("Unknown host");
            System.exit(2);
        }
        int rport = Integer.parseInt(args[2]);

        PortForwarderWithContext forwarder = new PortForwarderWithContext(lport, rhost, rport);
        forwarder.run();
    }
}
