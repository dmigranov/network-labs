package server.handlers;

import io.undertow.server.protocol.framed.AbstractFramedChannel;
import io.undertow.websockets.WebSocketConnectionCallback;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.xnio.ChannelListener;

public class WebsocketGetMessagesCallback implements WebSocketConnectionCallback {

    boolean hasToStop = false;
    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel webSocketChannel) {

        webSocketChannel.getCloseSetter().set(new ChannelListener<AbstractFramedChannel>() {
            @Override
            public void handleEvent(AbstractFramedChannel abstractFramedChannel) {
                System.out.println("CLOSE!");
                hasToStop = true;
            }
        });


        int t = new java.util.Random().nextInt();
        //System.out.println(t);
        while(!hasToStop) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("YO" + t);
            WebSockets.sendText("START", webSocketChannel, null);
        }
    }
}
