package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xnio.streams.ChannelOutputStream;
import server.Message;
import server.Messages;
import server.User;
import server.Users;

import java.nio.charset.StandardCharsets;

public class GetMessagesHandler extends AbstractRestHandler {
    public GetMessagesHandler(Users users, Messages messages) {
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
            else {
                User user = users.get(token);
                if (!user.isOnline()) {
                    user.setOnline(true);
                    messages.add(new Message(user.getUsername() + " returned", -1));
                }
                int count = exchange.getQueryParameters().get("count") != null ? Integer.parseInt(exchange.getQueryParameters().get("count").getFirst()) : 10;
                int offset = exchange.getQueryParameters().get("offset") != null ? Integer.parseInt(exchange.getQueryParameters().get("offset").getFirst()) : 0;
                if (offset > 100)
                    offset = 100;

                responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                Messages subMessages = offset + count <= messages.size() ? messages.subMessages(offset, offset + count) : messages.subMessages(offset, messages.size());

                JSONObject respObj = new JSONObject();
                JSONArray respArr = new JSONArray();

                for (int id = offset; id < offset + subMessages.size(); id++) {
                    Message msg = messages.get(id);
                    respArr.put(new JSONObject().put("id", id).put("message", msg.getMessage()).put("author", msg.getAuthorID()));
                }

                respObj.put("messages", respArr);
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
