package client;


import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MessageGetter implements Runnable {
    private final String serverAddress;
    private final String token;
    private final int uid;
    private final Users users;

    public MessageGetter(String serverAddress, String token, int uid, Users users) {
        this.serverAddress = "ws" + serverAddress.substring(4);
        this.token = token;
        this.uid = uid;
        this.users = users;
    }

    @Override
    public void run() {
        try {
            WebsocketClient client = new WebsocketClient(new URI(serverAddress + "/messages_ws"), token, uid);
        }
        catch(IOException | URISyntaxException | DeploymentException e) //TODO: нормальная обработка ошибок
        {
            e.printStackTrace();
        }
    }
}
