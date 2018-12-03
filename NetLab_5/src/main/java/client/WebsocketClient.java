package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebsocketClient
{
    Session session = null;

    WebsocketClient(URI uri) throws IOException, DeploymentException
    {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, uri);
    }
    @OnOpen
    public void onOpen(Session session)
    {
        System.out.println("WEBSOCKET OPENED");
        this.session = session;
    }
}
