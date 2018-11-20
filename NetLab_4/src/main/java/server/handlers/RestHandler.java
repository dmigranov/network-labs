package server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import org.json.JSONObject;
import org.xnio.streams.ChannelOutputStream;
import server.Messages;
import server.Users;

public class RestHandler implements HttpHandler {
    private Users users;
    private Messages messages;

    public RestHandler(Users users, Messages messages) {
        this.users = users;
        this.messages = messages;
    }

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
        String path = exchange.getRequestPath();
        ChannelOutputStream responseStream;

        /*try (ChannelInputStream bodyStream = new ChannelInputStream(exchange.getRequestChannel())) {
            String body = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            if (method.equals("POST")) {
                switch (path) {
                    case "/login":
                        System.out.println("i'm here and i shouldn't");
                        if (requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json")) {
                            reqObj = new JSONObject(body);
                            String username = reqObj.getString("username");
                            if (!users.containName(username)) {
                                responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                                User user = new User(username);
                                int id = users.add(user);
                                JSONObject respObject = new JSONObject();
                                respObject.put("id", id);
                                respObject.put("username", username);
                                respObject.put("online", user.isOnline());
                                respObject.put("token", user.getToken());
                                byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                                responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                                responseStream.write(jsonBytes);
                                responseStream.close();
                                messages.add(new Message(username + " joined in", -1));
                            } else {
                                exchange.setStatusCode(401);
                                responseHeaders.add(Headers.WWW_AUTHENTICATE, "Token realm = 'Username is already in use'");
                            }
                        } else
                            exchange.setStatusCode(400);
                        break;
                    case "/logout":
                        HeaderValues authorizationHeader;
                        if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null) {
                            //String username;
                            //if ((username = deleteUserWithToken(authorizationHeader.get(0).substring(6))) != null)
                            //{
                            User user = users.get(authorizationHeader.get(0).substring(6));
                            responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                            JSONObject respObject = new JSONObject();
                            respObject.put("message", "bye!");
                            byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                            responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                            responseStream.write(jsonBytes);
                            responseStream.close();
                            user.setOnline(false);
                            messages.add(new Message(user.getUsername() + " left", -1));
                            //}
                        } else
                            exchange.setStatusCode(400);
                        break;
                    case "/messages":
                        if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null && requestHeaders.get(Headers.CONTENT_TYPE) != null && requestHeaders.get(Headers.CONTENT_TYPE).get(0).equals("application/json")) {
                            String token = authorizationHeader.get(0).substring(6);
                            reqObj = new JSONObject(body);
                            String messageText = reqObj.getString("message");

                            Integer uid = users.getIDByToken(token);
                            if (uid == null) {
                                exchange.setStatusCode(403); //токен неизвестен серверу
                                break;
                            }

                            responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                            Message message = new Message(messageText, uid);
                            int id = messages.add(message);

                            byte[] jsonBytes = new JSONObject().put("id", id).put("message", messageText).toString().getBytes(StandardCharsets.UTF_8);
                            responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                            responseStream.write(jsonBytes);
                            responseStream.close();
                        } else
                            exchange.setStatusCode(400);
                        break;
                    default:
                        exchange.setStatusCode(405);
                        System.out.println(405);
                        break;
                }
            } else if (method.equals("GET")) {
                if (path.equals("/users")) {
                    HeaderValues authorizationHeader;
                    if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null) {
                        String token = authorizationHeader.get(0).substring(6);
                        Integer uid = users.getIDByToken(token);
                        //int uid = usr != null ?  usr.getId() : -1;
                        if (uid == null) {
                            exchange.setStatusCode(403);
                        } else {
                            responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                            JSONObject respObj = new JSONObject();
                            JSONArray respArr = new JSONArray();
                            for (uid = 0; uid < users.size(); uid++) {
                                User user = users.get(uid);
                                respArr.put(new JSONObject().put("id", uid).put("username", user.getUsername()).put("online", user.isOnline()));
                            }
                            respObj.put("users", respArr);
                            byte[] jsonBytes = respObj.toString().getBytes(StandardCharsets.UTF_8);
                            responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                            responseStream.write(jsonBytes);
                            responseStream.close();
                        }
                    } else
                        exchange.setStatusCode(400);
                } else if (path.matches("/users/(.+)")) {
                    HeaderValues authorizationHeader;
                    if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null) {
                        String token = authorizationHeader.get(0).substring(6);

                        Integer uid = users.getIDByToken(token);
                        if (uid == null) {
                            exchange.setStatusCode(403);
                        } else {
                            responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                            uid = Integer.parseInt(path.substring(7));
                            User user = users.get(uid);

                            if (user == null)
                                exchange.setStatusCode(404);
                            else {
                                JSONObject respObject = new JSONObject();
                                respObject.put("id", uid);
                                respObject.put("username", user.getUsername());
                                respObject.put("online", user.isOnline());
                                byte[] jsonBytes = respObject.toString().getBytes(StandardCharsets.UTF_8);
                                responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                                responseStream.write(jsonBytes);
                                responseStream.close();
                            }
                        }
                    } else
                        exchange.setStatusCode(400);

                }
                /*else if (path.equals("/messages")) {
                    HeaderValues authorizationHeader;
                    if ((authorizationHeader = requestHeaders.get(Headers.AUTHORIZATION)) != null) {
                        String token = authorizationHeader.get(0).substring(6);
                        Integer uid = users.getIDByToken(token);
                        if (uid == null) {
                            exchange.setStatusCode(403);
                        } else {
                            User user = users.get(token);
                            if (user.isOnline() == false) {
                                user.setOnline(true);
                                messages.add(new Message(user.getUsername() + " returned", -1));
                            }
                            int count = exchange.getQueryParameters().get("count") != null ? Integer.parseInt(exchange.getQueryParameters().get("count").getFirst()) : 10;
                            int offset = exchange.getQueryParameters().get("offset") != null ? Integer.parseInt(exchange.getQueryParameters().get("offset").getFirst()) : 0;
                            if (offset > 100)
                                offset = 100;

                            responseHeaders.add(Headers.CONTENT_TYPE, "application/json");
                            Messages subMessages = offset + count <= messages.size() ? messages.subMessages(offset, offset + count) : messages.subMessages(offset, messages.size());

                            JSONObject respObj = new JSONObject();
                            JSONArray respArr = new JSONArray();

                            for (int id = offset; id < offset + subMessages.size(); id++) {
                                Message msg = messages.get(id);
                                respArr.put(new JSONObject().put("id", id).put("message", msg.getMessage()).put("author", msg.getAuthorID()));
                            }

                            respObj.put("messages", respArr);
                            byte[] jsonBytes = respObj.toString().getBytes(StandardCharsets.UTF_8);
                            responseStream = new ChannelOutputStream(exchange.getResponseChannel());
                            responseStream.write(jsonBytes);
                            responseStream.close();
                        }
                    }
                }
                else {
                    exchange.setStatusCode(405);
                    System.out.println(405);
                }
            }
        } catch (IOException e) {
            exchange.setStatusCode(500);
        }*/
    }
}

//httpexchange encapsulates a http request received and a response to be generated in one exchange
//nb:: http1.0: не надо дополнительных заголовков. в http1.1 нужен пост, иначе сервер вернёт ошибку