package server;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.json.JSONObject;

public class WebsocketWriter {
    public static void write(Users users, Messages messages, String message)
    {
        for (int i = 0; i < users.size(); i++) {
            final User user = users.get(i);
            if(user.isOnline() && user.getWebSocketChannel() != null) {
                WebSockets.sendText(message, user.getWebSocketChannel(), new WebSocketCallback<Void>() {
                    @Override
                    public void onError(WebSocketChannel webSocketChannel, Void o, Throwable throwable) {
                        //user.setWentOffline(true);
                        user.setOnline(false);
                        int mid = messages.add(new Message(user.getUsername() + " left", -1));
                        String jsonString = new JSONObject().put("id", mid).put("message", user.getUsername() + " left").put("author", -1).toString();

                        WebsocketWriter.write(users, messages, jsonString);
                    }

                    @Override
                    public void complete(WebSocketChannel webSocketChannel, Void o) {}
                });
            }
        }
    }
}
