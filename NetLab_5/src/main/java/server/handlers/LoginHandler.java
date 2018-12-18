package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import org.json.JSONObject;
import org.xnio.streams.ChannelInputStream;
import org.xnio.streams.ChannelOutputStream;
import server.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class LoginHandler extends AbstractRestHandler {
    public LoginHandler(Users users, Messages messages) {
        super(users, messages);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        HeaderMap responseHeaders = exchange.getResponseHeaders();
        ChannelOutputStream responseStream;
        try (ChannelInputStream bodyStream = new ChannelInputStream(exchange.getRequestChannel()))
        {
            String body = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            if (requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json")) {
                JSONObject reqObj = new JSONObject(body);
                String username = reqObj.getString("username");
                if (!users.containName(username)) {
                    responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                    User user = new User(username);
                    int id = users.add(user);
                    JSONObject respObject = new JSONObject();
                    respObject.put("id", id);
                    respObject.put("username", username);
                    respObject.put("online", user.isOnline());
                    respObject.put("token", user.getToken());
                    byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                    responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                    responseStream.write(jsonBytes);
                    responseStream.close();
                    int mid = messages.add(new Message(username + " joined in", -1));

                    //!!!

                    String jsonString = new JSONObject().put("id", mid).put("message", username + " logged in").put("author", -1).toString();
                    WebsocketWriter.write(users, messages, jsonString);

                }
                else
                {
                    exchange.setStatusCode(401);
                    responseHeaders.add(Headers.WWW_AUTHENTICATE, "Token realm = 'Username is already in use'");
                }
            }
            else exchange.setStatusCode(400);
        }
    }
}
