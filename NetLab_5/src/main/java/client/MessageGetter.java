package client;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class MessageGetter
{
    @OnOpen
    public void onOpen(Session session)
    {

    }
}
