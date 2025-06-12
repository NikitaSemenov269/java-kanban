package testOfServers;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;
import servers.HttpTaskServer;
import tasks.Epic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpTaskManagerSubtasksTest {

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
    }

    @Test
    @Order(1)
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Desc A");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
        System.out.println("Тест testAddEpic пройден");
    }

    @Test
    @Order(2)
    public void testGetAllEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Epic 1", "Desc 1"));
        manager.createEpic(new Epic("Epic 2", "Desc 2"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length);
        System.out.println("Тест testGetAllEpics пройден");
    }

    @Test
    @Order(3)
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Get", "To retrieve");
        manager.createEpic(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic returned = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic Get", returned.getNameTask());
        assertEquals(id, returned.getId());
        System.out.println("Тест testGetEpicById пройден");
    }

    @Test
    @Order(4)
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic To Update", "Old Desc");
        manager.createEpic(epic);
        int id = epic.getId();

        Epic updated = new Epic("Updated Epic", "New Desc");
        updated.setId(id);

        String json = gson.toJson(updated);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + id))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic stored = manager.getEpic(id);
        assertEquals("Updated Epic", stored.getNameTask());
        assertEquals("New Desc", stored.getDescription());
        System.out.println("Тест testUpdateEpic пройден");
    }

    @Test
    @Order(5)
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic To Delete", "Bye");
        manager.createEpic(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertNull(manager.getEpic(id));
        System.out.println("Тест testDeleteEpicById пройден");
    }

    @Test
    @Order(6)
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Epic 1", "D1"));
        manager.createEpic(new Epic("Epic 2", "D2"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertTrue(manager.getAllEpics().isEmpty());
        System.out.println("Тест testDeleteAllEpics пройден");
    }
}
