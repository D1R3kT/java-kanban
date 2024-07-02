package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalTimeTypeAdapter;
import http.handler.HttpTaskServer;
import model.Status;
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

class HTTPPrioritizedTest {

    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;
    private static final String uri = "http://localhost:8080/prioritized";

    public HTTPPrioritizedTest() {
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
    void testGetPrioritized() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(Status.NEW, "Новая задача", "описание", LocalDateTime.parse("2024-12-21T21:21:21"), Duration.ofMinutes(15)));
        System.out.println("Create task" + task);

        Task task1 = manager.createTask(new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2025-12-20T21:21:21"), Duration.ofMinutes(105)));
        System.out.println("created " + task1);

        Task task2 = manager.createTask(new Task(Status.NEW, "работа", "1234",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15)));
        System.out.println("created " + task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray jsonElements = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(3, jsonElements.size());
        Assertions.assertEquals(task1, gson.fromJson(jsonElements.get(1), Task.class));
    }
}