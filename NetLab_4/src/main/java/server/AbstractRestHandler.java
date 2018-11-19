package server;

import io.undertow.server.HttpHandler;

public abstract class AbstractRestHandler implements HttpHandler {
    private Users users;
    private Messages messages;

    AbstractRestHandler(Users users, Messages messages) {
        this.users = users;
        this.messages = messages;
    }
}
