package server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpServerExchange;
import server.Messages;
import server.Users;

public class WebsocketGetMessagesHandler extends AbstractRestHandler {
    public WebsocketGetMessagesHandler(Users users, Messages messages) {
        super(users, messages);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Handlers.websocket(new WebsocketGetMessagesCallback()).handleRequest(exchange);
    }
}
