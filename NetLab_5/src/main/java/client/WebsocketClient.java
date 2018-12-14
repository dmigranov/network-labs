package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebsocketClient
{
    Session session = null;
    String token;
    int uid;

    WebsocketClient(URI uri, String token, int uid) throws IOException, DeploymentException
    {
        this.token = token;
        this.uid = uid;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, uri);
        //TODO: объединить webdocket client и MEssageGetter (WebsocketClient implements Runnable)

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
