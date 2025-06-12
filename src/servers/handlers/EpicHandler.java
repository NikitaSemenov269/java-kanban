package servers.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static managers.FileBackedTaskManager.formatter;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private int epicID;
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
        if (path[1].equals("epics")) {
            if (path.length == 2) {
                if (taskManager.getAllEpics().isEmpty()) {
                    sendNotFound(exchange);
                } else {
                    try {
                        sendText(exchange, 200, gson.toJson(taskManager.getAllEpics()));
                    } catch (NumberFormatException | NotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            } else if (path.length == 3) {
                try {
                    epicID = Integer.parseInt(path[2]);
                    Epic epic = taskManager.getEpic(epicID);
                    if (epic != null) {
                        sendText(exchange, 200, gson.toJson(epic));
                    }
                } catch (NumberFormatException | NotFoundException e) {
                    sendNotFound(exchange);
                }
            } else if (path.length == 4 && path[3].equals("subtasks")) {
                try {
                    epicID = Integer.parseInt(path[2]);
                    Epic epic = taskManager.getEpic(epicID);
                    if (epic != null) {
                        sendText(exchange, 200, gson.toJson(taskManager.getSubtasksOfEpic(epic)));
                    }
                } catch (NumberFormatException | NotFoundException e) {
                    sendNotFound(exchange);
                }
            } else {
                sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
            }
        }
    }

    private void handlePost(HttpExchange exchange, String[] path) throws IOException {
        if (path[1].equals("epics")) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8)) {
                try {
                    Epic epic = gson.fromJson(reader, Epic.class);
                    epic.setDuration(0);
                    if (epic.getNameTask() == null || epic.getDescription() == null) {
                        sendText(exchange, 400, "{\"error\": \"Имя и описание эпика обязательны.\"}");
                        return;
                    }
                    if (path.length == 2) {
                        epic.setTaskStatus(TaskStatus.NEW);
                        taskManager.createEpic(epic);
                        sendText(exchange, 200, "{\"message\": \"Эпик добавлен. Id: " + epic.getId() + "\"}");
                    } else if (path.length == 3) { // Обновление существующей задачи
                        int idEpic = Integer.parseInt(path[2]);
                        Epic existing = taskManager.getEpic(idEpic);
                        if (existing == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        Epic merged = mergeEpics(existing, epic);
                        if (taskManager.getPrioritizedTasks().stream()
                                .filter(t -> t.getId() != epicID) // исключаем саму задачу из проверки
                                .anyMatch(existing1 -> isTimeOverlap(existing1, merged))) {
                            sendHasInteractions(exchange);
                        } else {
                            taskManager.updateEpic(merged);
                            sendText(exchange, 200, "{\"message\": \"Эпик с id " + merged.getId() + " обновлен.\"}");
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
        if (path[1].equals("epics")) {
            try {
                if (path.length == 2) {
                    taskManager.deleteAllEpics();
                    sendText(exchange, 200, "{\"Все эпики удалены.\"}");
                } else if (path.length == 3) {
                    epicID = Integer.parseInt(path[2]);
                    taskManager.deleteEpicById(epicID);
                    sendText(exchange, 200, "{\"Эпик с id: " + epicID + " удален.\"}");
                }
            } catch (IOException | NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendText(exchange, 501, "{\"Некорректный endpoint.\"}");
        }
    }

    private Epic mergeEpics(Epic existing, Epic updates) {
        Epic merged = new Epic(existing.getNameTask(), existing.getDescription());
        merged.setId(existing.getId());
        merged.setNameTask(updates.getNameTask() != null ? updates.getNameTask() : existing.getNameTask());
        merged.setDescription(updates.getDescription() != null ? updates.getDescription() : existing.getDescription());
        merged.setTaskStatus(updates.getTaskStatus() != null ? updates.getTaskStatus() : existing.getTaskStatus());
        if (updates.getStartTime() != null) {
            merged.setStartTime(updates.getStartTime().format(formatter));
        } else if (existing.getStartTime() != null) {
            merged.setStartTime(existing.getStartTime().format(formatter));
        }
        if (updates.getDuration() != null) {
            merged.setDuration((int) updates.getDuration().toMinutes());
        } else if (existing.getDuration() != null) {
            merged.setDuration((int) existing.getDuration().toMinutes());
        }
        if (updates.getEndTime() != null) {
            merged.setEndTime(updates.getEndTime());
        } else if (existing.getEndTime() != null) {
            merged.setEndTime(existing.getEndTime());
        }
        List<Integer> mergedSubtasks = new ArrayList<>();
        if (existing.getIdSubtasks() != null) {
            mergedSubtasks.addAll(existing.getIdSubtasks());
        }
        if (updates.getIdSubtasks() != null) {
            for (Integer id : updates.getIdSubtasks()) {
                if (!mergedSubtasks.contains(id)) {
                    mergedSubtasks.add(id);
                }
            }
        }
        for (Integer id : mergedSubtasks) {
            merged.isAddIdSubtasks(id);
        }
        return merged;
    }
}