package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.json.JSONObject;
import org.xnio.streams.ChannelOutputStream;
import server.Messages;
import server.User;
import server.Users;

import java.nio.charset.StandardCharsets;

public class GetUserInfoHandler extends AbstractRestHandler {
    public GetUserInfoHandler(Users users, Messages messages) {
        super(users, messages);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        HeaderMap responseHeaders = exchange.getResponseHeaders();
        ChannelOutputStream responseStream;
        HeaderValues authorizationHeader;
        if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null) {
            String token = authorizationHeader.get(0).substring(6);

            Integer uid = users.getIDByToken(token);
            if (uid == null)
            {
                exchange.setStatusCode(403);
            }
            else
            {
                responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                uid = Integer.parseInt(exchange.getRequestPath().substring(7));
                User user = users.get(uid);

                if (user == null)
                    exchange.setStatusCode(404);
                else
                {
                    JSONObject respObject = new JSONObject();
                    respObject.put("id", uid);
                    respObject.put("username", user.getUsername());
                    respObject.put("online", user.isOnline());
                    byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                    responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                    responseStream.write(jsonBytes);
                    responseStream.close();
                }
            }
        }
        else
            exchange.setStatusCode(400);

    }
}
