package client;

import java.net.MalformedURLException;
import java.net.URL;

public class MessageRefresher implements Runnable {
    int currentMessageID = 0;
    //URL url;
    String serverAddress;


    MessageRefresher(String serverAddress) throws MalformedURLException
    {
        this.serverAddress = serverAddress;
    }


    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {}
            //create a get /messages req

        }
    }
}
