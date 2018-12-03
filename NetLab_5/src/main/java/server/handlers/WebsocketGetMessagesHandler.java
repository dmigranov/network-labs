package server.handlers;

import io.undertow.websockets.WebSocketConnectionCallback;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class WebsocketGetMessagesHandler implements WebSocketConnectionCallback {


    @Override
    public void onConnect(WebSocketHttpExchange webSocketHttpExchange, WebSocketChannel webSocketChannel) {
        System.out.println("YO");
    }
}
