package server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.InputStream;

public class RestHandler implements HttpHandler {
    /*@Override
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
    }*/

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String method = exchange.getRequestMethod().toString();        //POST
        HeaderMap headers = exchange.getRequestHeaders();
        String path = exchange.getRequestURI();

        System.out.println(method + " " + path + " " + headers.size());

        if(method.equals("POST"))
        {
            if(path.equals("/login"))
            {
                System.out.println("login");
                //System.out.println(reqHeaders.get("Content-Type").get(0)); //TODO: проверка на json
                //TODO: parse body!
            }
            else if (path.equals("/logout"))
            {
                System.out.println("logout");
            }
            else if(path.equals("/messages"))
            {
                System.out.println("messages");
            }
            else
            {
                //exchange.sendResponseHeaders(400, -1);
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
            else if (path.matches("/messages/+")) //do i need to specify parameters here?
            {
                System.out.println(path);
            }
            else
            {
                //exchange.sendResponseHeaders(400, -1);
            }
        }


    }
}

//httpexchange encapsulates a http request received and a response to be generated in one exchange
//nb:: http1.0: не надо дополнительных заголовков. в http1.1 нужен пост, иначе сервер вернёт ошибку