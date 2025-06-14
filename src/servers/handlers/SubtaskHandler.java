package servers.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static managers.FileBackedTaskManager.formatter;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private int subtaskId;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() != null && task1.getDuration() != null
                && task2.getStartTime() != null && task2.getEndTime() != null) {
            return task2.getStartTime().isBefore(task1.getEndTime())
                    && task2.getEndTime().isAfter(task1.getStartTime());
        }
        return false;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange, path);
                case "DELETE" -> handleDelete(exchange, path);
                default -> sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendText(exchange, 500, "{\"error\": \"Внутренняя ошибка сервера.\"}");
        }
    }

    private void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path[1].equals("subtasks")) {
            if (path.length == 2) {
                if (taskManager.getAllSubtasks().isEmpty()) {
                    sendNotFound(exchange);
                } else {
                    try {
                        sendText(exchange, 200, gson.toJson(taskManager.getAllSubtasks()));
                    } catch (NumberFormatException | NotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            } else if (path.length == 3) {
                try {
                    subtaskId = Integer.parseInt(path[2]);
                    Subtask subtask = taskManager.getSubtask(subtaskId);
                    if (subtask != null) {
                        sendText(exchange, 200, gson.toJson(taskManager.getSubtask(subtaskId)));
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException | NotFoundException e) {
                    sendNotFound(exchange);
                }
            }
        } else {
            sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
        }
    }

    private void handlePost(HttpExchange exchange, String[] path) throws IOException {
        if (path[1].equals("subtasks")) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8)) {
                try {
                    Subtask subtask = gson.fromJson(reader, Subtask.class);
                    if (subtask.getNameTask() == null || subtask.getDescription() == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    if (path.length == 2) {
                        if (subtask.getStartTime() == null) {
                            subtask.setStartTime(LocalDateTime.now().format(formatter));
                        }
                        if (subtask.getDuration() == null) {
                            subtask.setDuration(5);
                        }
                        Epic epic = taskManager.getEpic(subtask.getIdEpic());
                        if (epic == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        if (taskManager.getPrioritizedTasks().stream()
                                .filter(t -> t.getId() != subtaskId) // исключаем саму задачу из проверки
                                .anyMatch(existing1 -> isTimeOverlap(existing1, subtask))) {
                            sendHasInteractions(exchange);
                        } else {
                            taskManager.createSubtasks(subtask);
                            taskManager.updateEpicTime(epic);
                            sendText(exchange, 200, "{\"message\": \"Задача добавлена. Id: " + subtask.getId() + "\"}");
                        }
                    } else if (path.length == 3) {
                        subtaskId = Integer.parseInt(path[2]);
                        Subtask existing = taskManager.getSubtask(subtaskId);
                        subtask.setId(subtaskId);
                        if (existing == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        Subtask merged = mergeSubtasks(existing, subtask);
                        if (taskManager.getPrioritizedTasks().stream()
                                .filter(t -> t.getId() != subtaskId) // исключаем саму задачу из проверки
                                .anyMatch(existing1 -> isTimeOverlap(existing1, subtask))) {
                            sendHasInteractions(exchange);
                        } else {
                            taskManager.updateSubtask(subtask);
                            Epic epic = taskManager.getEpic(merged.getIdEpic());
                            taskManager.updateEpicTime(epic);  // Обновляем время эпика
                            sendText(exchange, 200, "{\"message\": \"Задача с id " + subtask.getId() + " обновлена.\"}");
                        }
                    }
                } catch (IOException | NumberFormatException | NotFoundException e) {
                    sendNotFound(exchange);
                } catch (JsonSyntaxException e) {
                    sendText(exchange, 400, "{\"error\": \"Неверный формат JSON.\"}");
                }
            }
        } else {
            sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
        }
    }

    private void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        if (path[1].equals("subtasks")) {
            try {
                if (path.length == 2) {
                    taskManager.deleteAllSubtasks();
                    sendText(exchange, 200, "{\"Все задачи удалены.\"}");
                } else if (path.length == 3) {
                    try {
                        int idSubtask = Integer.parseInt(path[2]);
                        Subtask subtask = taskManager.getSubtask(idSubtask);
                        if (subtask != null) {
                            Epic epic = taskManager.getEpic(subtask.getIdEpic());
                            taskManager.deleteSubtaskById(idSubtask);
                            taskManager.updateEpicTime(epic); // Обновляем время эпика
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException | NotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            } catch (IOException | NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
        }
    }

    private Subtask mergeSubtasks(Subtask existing, Subtask updates) {
        Subtask merged = new Subtask(
                updates.getNameTask() != null ? updates.getNameTask() : existing.getNameTask(),
                updates.getDescription() != null ? updates.getDescription() : existing.getDescription(),
                updates.getTaskStatus() != null ? updates.getTaskStatus() : existing.getTaskStatus(),
                existing.getIdEpic());
        merged.setId(existing.getId());

        merged.setStartTime(updates.getStartTime() != null ? updates.getStartTime().format(formatter)
                : existing.getStartTime().format(formatter));
        merged.setDuration((int) (updates.getDuration() != null ? updates.getDuration().toMinutes()
                : existing.getDuration().toMinutes()));
        return merged;
    }
}