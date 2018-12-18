package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.json.JSONObject;
import org.xnio.streams.ChannelInputStream;
import org.xnio.streams.ChannelOutputStream;
import server.Message;
import server.Messages;
import server.Users;
import server.WebsocketWriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class PostMessageHandler extends AbstractRestHandler {
    public PostMessageHandler(Users users, Messages messages) {
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

        try (ChannelInputStream bodyStream = new ChannelInputStream(exchange.getRequestChannel())) {
            String body = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            HeaderValues authorizationHeader;
            if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null && requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json")) {

                String token = authorizationHeader.get(0).substring(6);
                Integer uid = users.getIDByToken(token);
                if (uid == null) {
                    exchange.setStatusCode(403); //токен неизвестен серверу
                }
                else {
                    JSONObject reqObj = new JSONObject(body);
//                  reqObj = new JSONObject(body);
                    String messageText = reqObj.getString("message");

                    responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                    Message message = new Message(messageText, uid);
                    int id = messages.add(message);

                    String jsonString = new JSONObject().put("id", id).put("message", messageText).toString();
                    byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
                    //
                    responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                    responseStream.write(jsonBytes);
                    responseStream.close();

                    jsonString = new JSONObject().put("id", id).put("message", messageText).put("author", uid).toString();
                    WebsocketWriter.write(users, messages, jsonString);




                }
            }
            else
                exchange.setStatusCode(400);
        }
    }
}
