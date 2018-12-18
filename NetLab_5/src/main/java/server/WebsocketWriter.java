package server;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class WebsocketWriter {
    static void write(Users users, String message)
    {
        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            if(user.isOnline() && user.getWebSocketChannel() != null) {
                WebSockets.sendText(message, user.getWebSocketChannel(), new WebSocketCallback<Void>() {
                    @Override
                    public void onError(WebSocketChannel webSocketChannel, Void o, Throwable throwable) {
                        user.setWentOffline(true);
                    }

                    @Override
                    public void complete(WebSocketChannel webSocketChannel, Void o) {}
                });
            }
        }
    }
}
