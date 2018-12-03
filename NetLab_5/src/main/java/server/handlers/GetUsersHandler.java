package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xnio.streams.ChannelOutputStream;
import server.Messages;
import server.User;
import server.Users;

import java.nio.charset.StandardCharsets;

public class GetUsersHandler extends AbstractRestHandler {
    public GetUsersHandler(Users users, Messages messages) {
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
            if (uid == null) {
                exchange.setStatusCode(403);
            }
            else
            {
                responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                JSONObject respObj = new JSONObject();
                JSONArray respArr = new JSONArray();
                for (uid = 0; uid < users.size(); uid++) {
                    User user = users.get(uid);
                    respArr.put(new JSONObject().put("id", uid).put("username", user.getUsername()).put("online", user.isOnline()));
                }
                respObj.put("users", respArr);
                byte[] jsonBytes = respObj.toString().getBytes(StandardCharsets.UTF_8);
                responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                responseStream.write(jsonBytes);
                responseStream.close();
            }
        }
        else
            exchange.setStatusCode(400);

    }
}
