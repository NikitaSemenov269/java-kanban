package servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;

import java.io.IOException;

public class HistiryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    public HistiryHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}