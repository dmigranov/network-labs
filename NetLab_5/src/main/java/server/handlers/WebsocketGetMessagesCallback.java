package server.handlers;

import io.undertow.server.protocol.framed.AbstractFramedChannel;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.xnio.ChannelListener;
import server.Messages;
import server.User;
import server.Users;

import java.io.IOException;

public class WebsocketGetMessagesCallback implements WebSocketConnectionCallback {

    private final Users users;
    private final Messages messages;
    private int messageCounter = 0;
    boolean hasToStop = false;

    public WebsocketGetMessagesCallback(Users users, Messages messages) {
        this.users = users;
        this.messages = messages;
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel webSocketChannel) {
        messageCounter = messages.size();
        webSocketChannel.getCloseSetter().set(new ChannelListener<AbstractFramedChannel>() {
            @Override
            public void handleEvent(AbstractFramedChannel abstractFramedChannel) {
                /*System.out.println("CLOSE!");
                hasToStop = true;*/
            }
        });
        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                //super.onFullTextMessage(channel, message);
                String token = message.getData();
                User user = users.get(token);
                if (user == null)
                {
                    exchange.close();
                    webSocketChannel.close();
                }
                else
                {
                    user.setWebSocketChannel(webSocketChannel);
                    /*while(true)
                    {
                        int newMessageCounter;
                        System.out.println(messageCounter);
                        if((newMessageCounter = messages.size()) > messageCounter)
                        {
                            WebSockets.sendText("NEW MESSAGE", webSocketChannel, null);
                            messageCounter = newMessageCounter;

                        }
                    }*/

                }


            }});
        webSocketChannel.resumeReceives();

        //System.out.println(t);

    }
}
