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
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HTTPTasksTest {

    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;
    private static final String uri = "http://localhost:8080/tasks";

    public HTTPTasksTest() {
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
    @DisplayName("проверка создания задач")
    public void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("учеба", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldGetTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri + "/99");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15)));
        Task task2 = manager.createTask(new Task(Status.NEW, "работа", "1234",
                LocalDateTime.parse("2026-12-22T21:21:21"), Duration.ofMinutes(15)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        JsonArray jsonElements = JsonParser.parseString(response.body()).getAsJsonArray();
        Assertions.assertEquals(2, jsonElements.size());
        Task taskFromServer1 = gson.fromJson(jsonElements.get(0), Task.class);
        Task taskFromServer2 = gson.fromJson(jsonElements.get(1), Task.class);
        Assertions.assertEquals(task1, taskFromServer1);
        Assertions.assertEquals(task2, taskFromServer2);
    }

    @Test
    void shouldPostTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15)));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals(task.getName(), tasksFromManager.getFirst().getName());
    }

    @Test
    void shouldPostUpdateTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15)));


        Task updatetask = manager.updateTask(task);
        task.setId(task.getId());
        String taskJson = gson.toJson(updatetask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals(updatetask, tasksFromManager.getFirst());
    }
}