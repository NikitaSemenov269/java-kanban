package servers.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static managers.FileBackedTaskManager.formatter;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private int taskID;
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
        if (path[1].equals("tasks")) {
            if (path.length == 2) {
                if (taskManager.getAllTasks().isEmpty()) {
                    sendNotFound(exchange);
                } else {
                    try {
                        sendText(exchange, 200, gson.toJson(taskManager.getAllTasks()));
                    } catch (NumberFormatException | NotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            } else if (path.length == 3) {
                try {
                    taskID = Integer.parseInt(path[2]);
                    Task task = taskManager.getTask(taskID);
                    if (task != null) {
                        sendText(exchange, 200, gson.toJson(taskManager.getTask(taskID)));
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
        if (path[1].equals("tasks")) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8)) {
                try {
                    Task task = gson.fromJson(reader, Task.class);
                    if (task.getNameTask() == null || task.getDescription() == null) {
                        sendText(exchange, 400, "{\"error\": \"Имя и описание задачи обязательны.\"}");
                        return;
                    }
                    if (path.length == 2) {
                        task.setTaskStatus(TaskStatus.NEW);
                        if (task.getStartTime() == null) {
                            task.setStartTime(LocalDateTime.now().format(formatter));
                        }
                        if (task.getDuration() == null) {
                            task.setDuration(5);
                        }
                        if (taskManager.getPrioritizedTasks().stream()
                                .anyMatch(existing -> isTimeOverlap(existing, task))) {
                            sendHasInteractions(exchange);
                        } else {
                            taskManager.createTask(task);
                            sendText(exchange, 200, "{\"message\": \"Задача добавлена. Id: " + task.getId() + "\"}");
                        }
                    } else if (path.length == 3) { // Обновление существующей задачи
                        int idTask = Integer.parseInt(path[2]);
                        Task existing = taskManager.getTask(idTask);
                        if (existing == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        Task merged = mergeTasks(existing, task);
                        if (taskManager.getPrioritizedTasks().stream()
                                .filter(t -> t.getId() != idTask) // исключаем саму задачу из проверки
                                .anyMatch(existing1 -> isTimeOverlap(existing1, merged))) {
                            sendHasInteractions(exchange);
                        } else {
                            taskManager.updateTask(merged);
                            sendText(exchange, 200, "{\"message\": \"Задача с id " + merged.getId() + " обновлена.\"}");
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
        if (path[1].equals("tasks")) {
            try {
                if (path.length == 2) {
                    taskManager.deleteAllTasks();
                    sendText(exchange, 200, "{\"Все задачи удалены.\"}");
                } else if (path.length == 3) {
                    taskID = Integer.parseInt(path[2]);
                    taskManager.deleteTaskById(taskID);
                    sendText(exchange, 200, "{\"Задача с id: " + taskID + " удалена.\"}");
                }
            } catch (IOException | NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
        }
    }

    private Task mergeTasks(Task existing, Task updates) {
        Task merged = new Task(existing.getNameTask(), existing.getDescription());
        merged.setId(existing.getId());

        merged.setNameTask(updates.getNameTask() != null ? updates.getNameTask() : existing.getNameTask());
        merged.setDescription(updates.getDescription() != null ? updates.getDescription() : existing.getDescription());
        merged.setTaskStatus(updates.getTaskStatus() != null ? updates.getTaskStatus() : existing.getTaskStatus());
        merged.setStartTime(updates.getStartTime() != null ? updates.getStartTime().format(formatter)
                : existing.getStartTime().format(formatter));
        merged.setDuration(updates.getDuration() != null ? (int) updates.getDuration().toMinutes()
                : (int) existing.getDuration().toMinutes());

        return merged;
    }
}