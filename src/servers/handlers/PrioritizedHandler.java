package servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import servers.HttpTaskServer;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts[1].equals("prioritized")) {
            if (method.equals("GET")) {
                try {
                    sendText(exchange, 200, HttpTaskServer.gson.toJson(taskManager.getPrioritizedTasks()));
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
            }
        }
    }
}