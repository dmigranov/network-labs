package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public class RestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();        //POST
        Headers reqHeaders = exchange.getRequestHeaders();
        InputStream reqBody = exchange.getRequestBody();
        String path = exchange.getRequestURI().getPath();   //login etc.
        if(method.equals("POST"))
        {
            if(path.equals("/login"))
            {
                //System.out.println(reqHeaders.get("Content-Type").get(0)); //TODO: проверка на json
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
        else if(method.equals("GET"))
        {
            if (path.equals("/users"))
            {
                System.out.println("get users"); //TODO: проверка на json
                //TODO: parse body!
            }
            else if (path.matches("/users/(.+)"))
            {
                System.out.println(path);
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