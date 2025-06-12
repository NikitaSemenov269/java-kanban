package testOfServers;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;
import servers.HttpTaskServer;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HistiryHandlerTest {

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
        manager.deleteAllTasks();
        manager.deleteAllEpics();
    }

    @Test
    public void testGetHistoryEmpty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history);
        assertEquals(0, history.length);
        System.out.println("Тест testGetHistoryEmpty пройден");
    }

    @Test
    public void testGetHistoryAfterTaskAccess() throws IOException, InterruptedException {
        Task task = new Task("History Task", "For history test");
        manager.createTask(task);
        int id = task.getId();
        manager.getTask(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history);
        assertEquals(1, history.length);
        assertEquals(task.getNameTask(), history[0].getNameTask());
        System.out.println("Тест testGetHistoryAfterTaskAccess пройден");
    }
}
