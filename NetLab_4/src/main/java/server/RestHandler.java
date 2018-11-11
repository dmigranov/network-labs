package server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.json.JSONArray;
import org.xnio.streams.ChannelInputStream;
import org.json.JSONObject;
import org.xnio.streams.ChannelOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class RestHandler implements HttpHandler {
    static private List<User> users = new CopyOnWriteArrayList<>();
    static private List<Message> messages = new CopyOnWriteArrayList<>();

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        JSONObject reqObj;
        String method = exchange.getRequestMethod().toString();
        HeaderMap requestHeaders = exchange.getRequestHeaders();
        HeaderMap responseHeaders = exchange.getResponseHeaders();
        //String path = exchange.getRequestURI();
        String path = exchange.getRequestPath();
        ChannelOutputStream responseStream;

        try (ChannelInputStream bodyStream = new ChannelInputStream(exchange.getRequestChannel()))
        {
            String body = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            System.out.println(body);
            if (method.equals("POST")) {
                switch (path) {
                    case "/login":
                        if (requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json")) {
                            reqObj = new JSONObject(body);
                            String username = reqObj.getString("username");
                            if (!containsName(username)) {
                                responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                                User user = new User(username);
                                users.add(user);
                                JSONObject respObject = new JSONObject();
                                respObject.put("id", user.getId());
                                respObject.put("username", username);
                                respObject.put("online", true);
                                respObject.put("token", user.getToken());
                                System.out.println(user.getToken()); //удоли
                                byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                                responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                                responseStream.write(jsonBytes);
                                responseStream.close();
                                //TODO: добавить системное сообщение  о логине
                            }
                            else {
                                exchange.setStatusCode(401);
                                responseHeaders.add(Headers.WWW_AUTHENTICATE, "Token realm = 'Username is already in use'");
                            }
                        }
                        else
                            exchange.setStatusCode(400);
                        break;
                    case "/logout":
                        HeaderValues authorizationHeader;
                        if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null)
                        {
                            if (deleteUserWithToken(authorizationHeader.get(0).substring(6)))
                            {
                                JSONObject respObject = new JSONObject();
                                respObject.put("message", "bye!");
                                byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                                responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                                responseStream.write(jsonBytes);
                                responseStream.close();
                                //TODO: добавить системное сообщение  о логауте
                            }
                        }
                        else
                            exchange.setStatusCode(400);
                        break;
                    case "/messages":
                        if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null && requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json"))
                        {
                            String token = authorizationHeader.get(0).substring(6);
                            reqObj = new JSONObject(body);
                            String messageText = reqObj.getString("message");
                            Message message = new Message(messageText, findUser(token));
                            messages.add(message);
                            byte[] jsonBytes = new JSONObject().put("id", message.getId()).put("message", messageText).toString().getBytes(StandardCharsets.UTF_8);
                            responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                            responseStream.write(jsonBytes);
                            responseStream.close();
                        }
                        else
                            exchange.setStatusCode(400);
                        break;
                    default:
                        exchange.setStatusCode(405);
                        System.out.println(405);
                        break;
                }
            }

            else if (method.equals("GET"))
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
                else if (path.equals("/messages"))
                {
                    int count = Integer.parseInt(exchange.getQueryParameters().get("count").getFirst());
                    int offset = Integer.parseInt(exchange.getQueryParameters().get("offset").getFirst());
                    List<Message> messageSublist = offset + count <= messages.size() ? messages.subList(offset, offset + count) : messages.subList(offset, messages.size());
                    JSONObject respObj = new JSONObject();
                    JSONArray respArr = new JSONArray();
                    for (Message msg : messageSublist)
                    {
                        respArr.put(new JSONObject().put("id", msg.getId()).put("message", msg.getMessage()).put("author", msg.getAuthorID()));
                    }
                    respObj.put("messages", respArr);
                    System.out.println(respObj);

                }
                else
                {
                    exchange.setStatusCode(405);
                    System.out.println(405);
                }
            }
        }
        catch(IOException e)
        {
            exchange.setStatusCode(500);
        }
    }

    private int findUser(String token) {
        for (User user : users)//synchro?
        {
            if (user.getToken().equals(token))
                return user.getId();
        }
        return -1;
    }


    private boolean containsName(String username) {
        for (User user : users)//synchro?
        {
            if (user.getUsername().equals(username))
                return true;
        }
        return false;
    }

    private boolean deleteUserWithToken(String token)
    {
        for (User user : users)//synchro?
        {
            if (user.getToken().equals(token)) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }
}

//httpexchange encapsulates a http request received and a response to be generated in one exchange
//nb:: http1.0: не надо дополнительных заголовков. в http1.1 нужен пост, иначе сервер вернёт ошибку