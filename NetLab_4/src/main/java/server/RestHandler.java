package server;


import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

//import org.json.*;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.streams.ChannelInputStream;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.StandardCharsets;


public class RestHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) //throws Exception
    {
        if(exchange.isInIoThread())
        {
            exchange.dispatch(this);
            return;
        }
        String method = exchange.getRequestMethod().toString();        //POST
        HeaderMap headers = exchange.getRequestHeaders();
        String path = exchange.getRequestURI();
        //StreamSourceChannel bodyChannel = exchange.getRequestChannel();
        //ChannelInputStream bodyStream = new ChannelInputStream(bodyChannel); //if there is no req body, calling this method may cause the next req to be processed. NB: close()!
        //String body = new BufferedReader(new InputStreamReader(bodyStream)).lines().collect(Collectors.joining("\n"));
        byte[] bodyData;
        exchange.getRequestReceiver().receiveFullString(new Receiver.FullStringCallback() {
            @Override
            public void handle(HttpServerExchange httpServerExchange, String s) {
                System.out.println("BODY: " + s);
                //exchange.getResponseSender().send("");
                exchange.getResponseHeaders().add(Headers.HOST, "localhost");
            }
        }); //charset!!!
        //String body = null;
        /*try {
            System.out.println("i'm here");
            body = IOUtils.toString(bodyStream, "UTF8");
        }
        catch(IOException e)
        {
            //TODOclose stream?
            exchange.setStatusCode(500);
        }*/


        if(method.equals("POST"))
        {
            switch (path) {
                case "/login":
                    System.out.println("login");
                    if (headers.get("Content-Type") != null && headers.get("Content-Type").get(0).equals("application/json")) {
                        System.out.println("JSON"); //TODO: проверка на json
                        //TODO: parse body!

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
                    break;
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
                exchange.setStatusCode(405);
            }
        }


    }
}

//httpexchange encapsulates a http request received and a response to be generated in one exchange
//nb:: http1.0: не надо дополнительных заголовков. в http1.1 нужен пост, иначе сервер вернёт ошибку