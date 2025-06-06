package servers;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import servers.handlers.*;
import com.google.gson.Gson;
import servers.typeAdapters.DurationAdapter;
import servers.typeAdapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private static TaskManager taskManager = Managers.getDefault();

    public static final int PORT = 8080;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    //метод для удобного запуска сервера.
    public static void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, historyManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager, historyManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, historyManager));
        httpServer.createContext("/history", new HistiryHandler(taskManager, historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, historyManager));
    }


    public static void main(String[] args) {

    }
}

