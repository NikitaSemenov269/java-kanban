package servers.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange h, int code, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(code, resp.length);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendText(h, 404, "{\"Задача не найдена\"}");
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        sendText(h, 406, "{\"Задача имеет пересечение временных интервалов с другой задачей" +
                " и не может быть добавлена в приоритетный список.\"}");
    }
}