package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebsocketClient
{
    Session session = null;
    String token;

    WebsocketClient(URI uri, String token) throws IOException, DeploymentException
    {
        this.token = token;
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
}
