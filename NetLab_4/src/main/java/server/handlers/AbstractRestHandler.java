package server.handlers;

import io.undertow.server.HttpHandler;
import server.Messages;
import server.Users;

public abstract class AbstractRestHandler implements HttpHandler {
    Users users;
    Messages messages;


    AbstractRestHandler(Users users, Messages messages) {
        this.users = users;
        this.messages = messages;
    }
}
