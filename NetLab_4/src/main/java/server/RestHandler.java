package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;

public class RestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();        //POST
        Headers responseHeaders = exchange.getResponseHeaders();

        String path = exchange.getRequestURI().getPath();   //login etc.
        if(method.equals("POST"))
        {
            if(path.equals("/login"))
            {
                System.out.println("login");
                //TODO: parse body!
            }
            else if (path.equals("/logout"))
            {
                System.out.println("logout");
            }
            else
            {
                exchange.sendResponseHeaders(400, -1);
            }
        }

        //System.out.println(exchange.getRequestHeaders());
    }
}

//httpexchange encapsulates a http request received and a response to be generated in one exchange