package server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import server.Messages;
import server.Users;
import server.factory.Factory;

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
        if ("POST".equals(method) || "GET/messages".equals(method + path)) {
            AbstractRestHandler handler = factory.getHandler(method + path);
            handler.handleRequest(exchange); //расскоментить когда готово
        }
        else {
            //System.out.println(method + path);
            //todo: FactoryException catch
            HttpHandler rh = new RestHandler(users, messages);
            rh.handleRequest(exchange);
        }
    }
}
