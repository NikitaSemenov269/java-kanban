package servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.exceptions.NotFoundException;
import managers.interfaces.HistoryManager;
import servers.HttpTaskServer;

import java.io.IOException;

public class HistiryHandler extends BaseHttpHandler implements HttpHandler {
    private final HistoryManager historyManager;

    public HistiryHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] path = exchange.getRequestURI().getPath().split("/");
            if (method.equals("GET")) {
                handleGet(exchange, path);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendText(exchange, 500, "{\"error\": \"Внутренняя ошибка сервера.\"}");
        }
    }

    private void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path[1].equals("history") && path.length == 2) {
            try {
                sendText(exchange, 200, HttpTaskServer.gson.toJson(historyManager.getHistory()));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        }
    }
}