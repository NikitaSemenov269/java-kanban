package testOfServers;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;
import servers.HttpTaskServer;
import tasks.Epic;
import tasks.enums.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        manager.deleteAllTasks();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description Epic 1");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(manager.getAllEpics().isEmpty());
        assertEquals("Epic 1", manager.getAllEpics().get(0).getNameTask());
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Epic A", "Desc A"));
        manager.createEpic(new Epic("Epic B", "Desc B"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length);
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Single Epic", "Desc");
        manager.createEpic(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Single Epic", returnedEpic.getNameTask());
        assertEquals(id, returnedEpic.getId());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("ToDelete", "Desc");
        manager.createEpic(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getEpic(id), "Эпик должен быть удалён");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic("E1", "D1"));
        manager.createEpic(new Epic("E2", "D2"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Original", "Before update");
        manager.createEpic(epic);
        int id = epic.getId();

        Epic updated = new Epic("Updated Epic", "After update");
        updated.setId(id);
        updated.setTaskStatus(TaskStatus.IN_PROGRESS);

        String json = gson.toJson(updated);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + updated.getId()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic stored = manager.getEpic(id);
        assertEquals("Updated Epic", stored.getNameTask());
        assertEquals(TaskStatus.IN_PROGRESS, stored.getTaskStatus());
    }
}

