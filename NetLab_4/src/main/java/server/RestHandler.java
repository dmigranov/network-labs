package server;


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;

import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.json.JSONObject;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.streams.ChannelInputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class RestHandler implements HttpHandler {


    @Override
    public void handleRequest(HttpServerExchange exchange) //throws Exception
    {
        if(exchange.isInIoThread())
        {
            exchange.dispatch(this);
            return;
        }
        JSONObject obj;
        String method = exchange.getRequestMethod().toString();
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        HeaderMap responseHeaders = exchange.getResponseHeaders();
        String path = exchange.getRequestURI();

        StreamSourceChannel bodyChannel = exchange.getRequestChannel();
        ChannelInputStream bodyStream = new ChannelInputStream(bodyChannel); //if there is no req body, calling this method may cause the next req to be processed. NB: close()!
        String body = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")); //this is blocking

        System.out.println(body);

        if(method.equals("POST"))
        {
            switch (path) {
                case "/login":
                    System.out.println("login");
                    if (requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json")) {
                        obj = new JSONObject(body);
                        System.out.println(obj.getString("username"));
                        //generate token
                        responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                    }
                    else
                        exchange.setStatusCode(400);
                    break;
                case "/logout":
                    System.out.println("logout");
                    break;
                case "/messages":
                    System.out.println("messages");
                    break;
                default:
                    exchange.setStatusCode(405);
                    System.out.println(405);
                    break;
            }
        }
        else if(method.equals("GET"))
        {
            if (path.equals("/users"))
            {
                System.out.println("get users");
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
                exchange.setStatusCode(405);
                System.out.println(405);
            }
        }


    }
}

//httpexchange encapsulates a http request received and a response to be generated in one exchange
//nb:: http1.0: не надо дополнительных заголовков. в http1.1 нужен пост, иначе сервер вернёт ошибку