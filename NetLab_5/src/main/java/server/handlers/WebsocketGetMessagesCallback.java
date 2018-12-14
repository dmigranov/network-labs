package server.handlers;

import io.undertow.server.protocol.framed.AbstractFramedChannel;
import io.undertow.websockets.WebSocketConnectionCallback;

import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.xnio.ChannelListener;
import server.Messages;
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

        webSocketChannel.getCloseSetter().set(new ChannelListener<AbstractFramedChannel>() {
            @Override
            public void handleEvent(AbstractFramedChannel abstractFramedChannel) {
                System.out.println("CLOSE!");
                hasToStop = true;
            }
        });
        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                //super.onFullTextMessage(channel, message);
                System.out.println(message.getData());
            }});
        webSocketChannel.resumeReceives();

        //WebSockets.sendText("START", webSocketChannel, null);

        //System.out.println(t);
        /*while(!hasToStop) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("YO");

        }*/
    }
}
