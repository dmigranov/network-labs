package server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class CoreHandler implements HttpHandler
{
    private Users users;
    private Messages messages;


    CoreHandler(Users users, Messages messages) {
        this.users = users;
        this.messages = messages;
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpHandler rh = new RestHandler(users, messages);
        rh.handleRequest(exchange);
    }
}
