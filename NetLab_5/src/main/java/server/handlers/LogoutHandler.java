package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.json.JSONObject;
import org.xnio.streams.ChannelOutputStream;
import server.*;

import java.nio.charset.StandardCharsets;

public class LogoutHandler extends AbstractRestHandler {
    public LogoutHandler(Users users, Messages messages) {
        super(users, messages);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        HeaderMap responseHeaders = exchange.getResponseHeaders();
        ChannelOutputStream responseStream;

        HeaderValues authorizationHeader;
        if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null)
        {
            User user = users.get(authorizationHeader.get(0).substring(6));
            responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
            JSONObject respObject = new JSONObject();
            respObject.put("message", "bye!");
            byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
            responseStream = new ChannelOutputStream(exchange.getResponseChannel());
            responseStream.write(jsonBytes);
            responseStream.close();
            user.setOnline(false);
            int mid = messages.add(new Message(user.getUsername() + " left", -1));

            //!!!
            String jsonString = new JSONObject().put("id", mid).put("message", user.getUsername() + " left").put("author", -1).toString();
            WebsocketWriter.write(users, messages, jsonString);
            /*for (int i = 0; i < users.size(); i++) {
                user = users.get(i);
                if(user.isOnline() && user.getWebSocketChannel() != null) {
                    WebSockets.sendText(jsonString, user.getWebSocketChannel(), null);
                }
            }*/
            //!!!
        }
        else
            exchange.setStatusCode(400);
    }
}
