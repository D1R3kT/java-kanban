package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getEpic(exchange);
            case GET_ALL -> getAllEpics(exchange);
            case GET_EPIC_SUBTASKS -> getEpicSubtasks(exchange);
            case POST -> postEpic(exchange);
            case DELETE -> deleteEpic(exchange);
            case UNKNOWN -> handleUnknown(exchange);
        }
    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        int taskId = getTaskId(exchange);
        try {
            Epic epic = taskManager.getEpicById(taskId);
            SubTask epicSubtasksById = taskManager.getAllSubTasks(epic);
            String reply = gson.toJson(epicSubtasksById);
            sendText(exchange, reply);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        int taskId = getTaskId(exchange);
        try {
            taskManager.removeByIdEpic(taskId);
            sendOK(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void postEpic(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic epic = gson.fromJson(requestBody, Epic.class);
            if (epic.getId() > 0) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.createEpic(epic);
            }
            sendCreated(exchange);
        } catch (ValidationException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleUnknown(HttpExchange exchange) throws IOException {
        sendNotFound(exchange, "Error in path!");
    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllEpics()));
    }

    private void getEpic(HttpExchange exchange) throws IOException, NotFoundException {
        int taskId = getTaskId(exchange);
        try {
            Epic taskById = taskManager.getEpicById(taskId);
            String reply = gson.toJson(taskById);
            sendText(exchange, reply);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}