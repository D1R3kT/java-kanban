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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPEpicsTest {

    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;
    private static final String uri = "http://localhost:8080/epics";

    public HTTPEpicsTest() {
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
    void shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic(1, "Новый эпик", Status.NEW,
                "описание", LocalDateTime.now(), Duration.ofMinutes(15)));


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic, epicFromServer);
    }


    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("task1", "test task");
        Epic epic2 = new Epic("task2", "test task");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray jsonElements = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(2, jsonElements.size());
        Epic taskFromServer1 = gson.fromJson(jsonElements.get(0), Epic.class);
        Epic taskFromServer2 = gson.fromJson(jsonElements.get(1), Epic.class);
        Assertions.assertEquals(epic1, taskFromServer1);
        Assertions.assertEquals(epic2, taskFromServer2);
    }

    @Test
    void shouldPostEpic() throws IOException, InterruptedException {
        Epic task = new Epic("task", "test task");
        String taskJson = gson.toJson(task);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());

    }


    @Test
    void testPostUpdateEpicNotFound() throws IOException, InterruptedException {
        Epic taskUP = new Epic("taskUP", "test task");
        taskUP.setId(1);
        String taskJson = gson.toJson(taskUP);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic task = new Epic("task", "test task");
        manager.createEpic(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, manager.getAllEpics().size());
    }


}

