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
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HTTPSubtasksTest {

    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;
    Epic epic = manager.createEpic(new Epic("Новый эпик", "описание"));
    static String uri = "http://localhost:8080/subtasks";

    public HTTPSubtasksTest() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter());
        gson = gsonBuilder.create();
    }

    @BeforeEach
    public void setUp() {
        taskServer.start();
        manager.createEpic(epic);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void shouldGetSubtask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(Status.NEW, "Новая задача", "описание подзадачи",
                epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30));
        manager.createSubTask(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }


    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        SubTask subTask1 = new SubTask(Status.DONE, "Подзадача 1", "Описание1",
                epic, LocalDateTime.parse("2025-12-22T21:21:21"), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask(Status.NEW, "Подзадача 2", "Описание2", epic,
                LocalDateTime.parse("2025-12-23T21:21:21"), Duration.ofMinutes(30));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray jsonElements = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(2, jsonElements.size());
        SubTask taskFromServer1 = gson.fromJson(jsonElements.get(0), SubTask.class);
        SubTask taskFromServer2 = gson.fromJson(jsonElements.get(1), SubTask.class);
        assertEquals(subTask1, taskFromServer1);
        assertEquals(subTask2, taskFromServer2);
    }

    @Test
    void shouldPostSubtask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(Status.NEW, "Новая задача", "описание подзадачи",
                epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30));
        String taskJson = gson.toJson(subTask);

        //http client
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        //rest call
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //response
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals(subTask.getName(), tasksFromManager.getFirst().getName());
    }

    @Test
    void shouldPostUpdateSubtask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(Status.NEW, "Новая задача", "описание подзадачи",
                epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30));
        manager.createSubTask(subTask);
        manager.updateSubTask(subTask);


        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals(subTask, tasksFromManager.getFirst());
    }

    @Test
    void shouldPostUpdateSubtaskNotFound() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(Status.NEW, "Новая задача", "описание подзадачи",
                epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30));
        subTask.setId(1);
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldPostUpdateSubtaskIntercept() throws IOException, InterruptedException {
        SubTask subTask1 = new SubTask(Status.DONE, "Подзадача 1", "Описание1",
                epic, LocalDateTime.parse("2025-12-22T21:21:21"), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask(Status.NEW, "Подзадача 2", "Описание2", epic,
                LocalDateTime.parse("2025-12-23T21:21:21"), Duration.ofMinutes(30));
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        subTask1.setStatus(Status.IN_PROGRESS);


        String taskJson = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(Status.NEW, "Новая задача", "описание подзадачи",
                epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30));
        manager.createSubTask(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri + "/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubTasks().size());
    }
}