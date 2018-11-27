package ru.nsu.migranov.portforwarder;

import java.net.InetAddress;

public class PortForwarder {
    private int lport;
    private InetAddress rhost;
    private int rport;
    public PortForwarder(int lport, InetAddress rhost, int rport) {
        this.lport = lport;
        this.rhost = rhost;
        this.rport = rport;
    }

}
