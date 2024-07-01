package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalTimeTypeAdapter;
import http.handler.HttpTaskServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;


class HTTPHistoryTest {

    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;
    private static final String uri = "http://localhost:8080/history";

    public HTTPHistoryTest() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter());
        gson = gsonBuilder.create();
    }

    @BeforeEach
    public void setUp() {
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {

        Task task = new Task(Status.NEW, "Новая задача", "описание",
                LocalDateTime.parse("2024-12-21T21:21:21"), Duration.ofMinutes(15));

        Task task1 = new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2025-12-20T21:21:21"), Duration.ofMinutes(105));

        Task task2 = new Task(Status.NEW, "работа", "1234",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15));

        Epic epic = new Epic("epic1", "epic description1");
        manager.createEpic(epic);
        SubTask subTask = new SubTask(Status.NEW, "Новая задача", "описание подзадачи",
                epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30));

        manager.createSubTask(subTask);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task);
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic.getId());
        manager.getTaskById(task1.getId());
        manager.getSubtaskById(subTask.getId());
        manager.getTaskById(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray jsonElements = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(5, jsonElements.size());
        Assertions.assertEquals(epic, gson.fromJson(jsonElements.get(1), Epic.class));
    }
}