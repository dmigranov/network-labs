package server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import server.Messages;
import server.Users;
import server.factory.Factory;
import server.factory.FactoryException;

public class RootHandler implements HttpHandler
{
    private Users users;
    private Messages messages;


    public RootHandler(Users users, Messages messages) {
        this.users = users;
        this.messages = messages;
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Factory factory = Factory.getInstance();
        factory.init(users, messages);
        String method = exchange.getRequestMethod().toString();
        String path = exchange.getRequestPath();

        /*if (path.equals("/messages_ws"))
            Handlers.websocket(new WebsocketGetMessagesCallback()).handleRequest(exchange); //порнография конечно

        else */{
            if (path.matches("/users/(.+)"))
                path = "/users/id";
            try {
                AbstractRestHandler handler = factory.getHandler(method + path);
                handler.handleRequest(exchange);
            } catch (FactoryException e) {
                exchange.setStatusCode(500);
            }
        }
    }
}
