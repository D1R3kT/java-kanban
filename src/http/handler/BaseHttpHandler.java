package http.handler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    Gson gson;

    public BaseHttpHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter());

        gson = gsonBuilder.create();
    }


    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        sendResponse(httpExchange, 200, text);
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        sendResponse(httpExchange, 404, text);
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        sendResponse(httpExchange, 406, text);
    }

    protected void sendCreated(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, 201);
    }

    protected void sendOK(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, 200);
    }

    private void sendResponse(HttpExchange httpExchange, int code) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);
        httpExchange.getResponseBody().close();
    }

    private void sendResponse(HttpExchange h, int code, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_ALL;
            }
            if (pathParts.length == 3 && isNumeric(pathParts[2])) {
                return Endpoint.GET;
            }
            if (pathParts.length == 4 && isNumeric(pathParts[2]) && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPIC_SUBTASKS;
            }
        }
        if (requestMethod.equals("POST")) {
            return Endpoint.POST;
        }
        if (requestMethod.equals("DELETE") && isNumeric(pathParts[2])) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }

    protected int getTaskId(HttpExchange exchange) {
        return Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
    }
}
