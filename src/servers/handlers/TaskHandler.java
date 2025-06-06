package servers.handlers;

//import com.google.gson.GsonBuilder;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import managers.interfaces.HistoryManager;
//import managers.interfaces.TaskManager;
//import com.google.gson.Gson;
//import servers.HttpTaskServer;
//import servers.typeAdapters.DurationAdapter;
//import servers.typeAdapters.LocalDateTimeAdapter;
//import tasks.Task;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.time.LocalDateTime;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    public TaskHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        switch (method) {
            case "GET":
                try {
                    if (parts.length == 2) {
                        sendText(exchange, 200, HttpTaskServer.gson.toJson(taskManager.getAllTasks()));
                    } else if (parts.length >= 3) {
                        sendText(exchange, 200, HttpTaskServer.gson.toJson(taskManager
                                .getTask(Integer.parseInt(parts[2]))));
                        break;
                    }
                } catch (NullPointerException e) {
                    sendNotFound(exchange);
                } catch (NumberFormatException e) {
                    System.err.println("Некорректное значение id задачи");
                }
            case "POST":
                if (parts.length == 2) {

                    // taskManager.createTask(task);
                } else if (parts.length >= 3) {
                    // taskManager.updateTask(task);
                }
                break;

            case "DELETE":
                if (parts.length >= 3) {
                    // taskManager.deleteTaskById(Integer.parseInt(parts[2]));

                }
                break;
            default:

        }


    }
}

