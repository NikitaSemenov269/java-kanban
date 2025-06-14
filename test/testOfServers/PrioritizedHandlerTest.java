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

public class PrioritizedHandlerTest {

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
    public void testGetPrioritizedTasksEmpty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasks);
        assertEquals(0, tasks.length);
        System.out.println("Тест testGetPrioritizedTasksEmpty пройден");
    }

    @Test
    public void testGetPrioritizedTasksOrder() throws IOException, InterruptedException {
        // Добавим задачи в разном порядке
        Task t1 = new Task("Task1", "First");
        t1.setStartTime("20.12.2024 10:00");
        Task t2 = new Task("Task2", "Second");
        t2.setStartTime("24.12.2024 10:00");
        Task t3 = new Task("Task3", "Third");
        t3.setStartTime("27.12.2024 10:00");

        manager.createTask(t2);
        manager.createTask(t3);
        manager.createTask(t1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasks);
        assertEquals(3, tasks.length);

        assertEquals("Task1", tasks[0].getNameTask());
        assertEquals("Task2", tasks[1].getNameTask());
        assertEquals("Task3", tasks[2].getNameTask());

        System.out.println("Тест testGetPrioritizedTasksOrder пройден");
    }
}
