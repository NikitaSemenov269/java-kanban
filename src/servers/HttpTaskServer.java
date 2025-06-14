package servers;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import servers.handlers.*;
import com.google.gson.Gson;
import servers.typeAdapters.DurationAdapter;
import servers.typeAdapters.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final HistoryManager historyManager;

    public static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.historyManager = ((InMemoryTaskManager) taskManager).getHistoryManager();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        initContexts();
    }

    private void initContexts() {
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = new FileBackedTaskManager(new File("autosave.csv"));
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}