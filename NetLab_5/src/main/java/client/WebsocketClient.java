package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebsocketClient implements Runnable
{
    Session session = null;
    String token;
    int uid;

    WebsocketClient(String serverAddress, String token, int uid, Users users) throws IOException, DeploymentException, URISyntaxException
    {
        this.token = token;
        this.uid = uid;
        URI uri = new URI("ws" + serverAddress.substring(4) + "/messages_ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, uri);


    }
    @OnOpen
    public void onOpen(Session session) throws IOException
    {
        System.out.println("WEBSOCKET OPENED");
        this.session = session;
        session.getBasicRemote().sendText(token);

    }

    @OnMessage
    public void onMessage(Session session, String s)
    {
        System.out.println(s);

    }

    @Override
    public void run() {

    }
}
