package managers;

import managers.exceptions.NotFoundException;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.*;
import tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private Comparator<Task> startTime = Comparator.nullsLast(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            )
    );
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(startTime);

    //  ГЕНЕРАЦИЯ ID.
    private int generateId() {
        if (id < CSVFormat.getGenerateId()) {
            id = CSVFormat.getGenerateId();
        }
        return ++id;
    }

    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    //  ОБНОВЛЕНИЕ СТАТУСА КОНКРЕТНОГО EPICа.
    private void updateEpicStatus(Epic epic) {
        List<TaskStatus> flags = epic.getIdSubtasks().stream()
                .map(subtasks::get)
                .map(Subtask::getTaskStatus)
                .collect(Collectors.toList());

        if (flags.contains(TaskStatus.DONE) && flags.contains(TaskStatus.NEW)
                || flags.contains(TaskStatus.IN_PROGRESS)) {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (flags.contains(TaskStatus.DONE) && !flags.contains(TaskStatus.NEW)
                && !flags.contains(TaskStatus.IN_PROGRESS)) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    //Расчет временных показателей Epic
    public void updateEpicTime(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic не может быть null");
        }
        try {
            List<Subtask> subtaskList = epic.getIdSubtasks().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Если сабтасков нет — сбрасываем время
            if (subtaskList.isEmpty()) {
                epic.setStartTimeEpic(null);
                epic.setDuration(0);
                epic.setEndTime(null);
                return;
            }
            Optional<LocalDateTime> startTime = epic.getIdSubtasks().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder());

            epic.setStartTimeEpic(startTime.orElse(null));

            long durationSum = epic.getIdSubtasks().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .mapToLong(Duration::toMinutes)
                    .sum();

            epic.setDuration((int) durationSum);

            Optional<LocalDateTime> endTime = epic.getIdSubtasks().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .max(Comparator.comparing(Subtask::getStartTime))
                    .map(subtask -> subtask.getStartTime().plus(subtask.getDuration()));

            epic.setEndTime(endTime.orElse(null));
            if (epic.getStartTime() != null || epic.getEndTime() != null) {
                updateEpic(epic);
            }
        } catch (Exception e) {
            //при ошибке возвращаем дефолтные значения
            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epic);
            }
            System.err.println("Ошибка при расчёте времени Epic: " + e.getMessage());
            epic.setStartTime("01.01.0001 00:00");
            epic.setDuration(0);
            epic.setEndTime((null));
        }
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() != null && task1.getDuration() != null
                && task2.getStartTime() != null && task2.getEndTime() != null) {
            return task2.getStartTime().isBefore(task1.getEndTime())
                    && task2.getEndTime().isAfter(task1.getStartTime());
        }
        return false;
    }

    //  CОЗДАНИЕ ЗАДАЧ.
    @Override
    public void createTask(Task task) throws NotFoundException {
        if (task != null) {
            int newId = generateId();
            task.setId(newId);
            tasks.put(task.getId(), task);  // добавляем в список tasks
            if (task.getStartTime() != null
                    && task.getEndTime() != null
                    && task.getEndTime().isAfter(task.getStartTime())) {
                if (task.getStartTime() != null && task.getDuration() != null) {
                    if (!prioritizedTasks.isEmpty()) {
                        boolean hasOverlap = prioritizedTasks.stream()
                                .anyMatch(task1 -> isTimeOverlap(task1, task));
                        if (!hasOverlap) {
                            prioritizedTasks.add(task);
                        }
                    } else {
                        prioritizedTasks.add(task);
                    }
                }
            }
        }
    }

    @Override
    public void createEpic(Epic epic) throws NotFoundException {
        if (epic != null) {
            int newId = generateId();
            epic.setId(newId);
            epics.put(epic.getId(), epic);
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    //  СОЗДАНИЕ SUBTASK В КОНКРЕТНОМ EPICe.
    @Override
    public void createSubtasks(Subtask subtask) throws NotFoundException {
        //получаем экземпляр подходящего Tasks.tasks.src.javakanban.Epic.
        Epic epic = epics.get(subtask.getIdEpic());
        //проверка наличия ключа в epics.
        if (epic != null) {
            //добавление id в список id хранящихся в Tasks.tasks.src.javakanban.Epic.
            int newId = generateId();
            subtask.setId(newId);
            if (epic.isAddIdSubtasks(subtask.getId())) {
                subtasks.put(subtask.getId(), subtask);
                if (subtask.getStartTime() != null && subtask.getEndTime() != null
                        && subtask.getStartTime().isBefore(subtask.getEndTime())) {
                    if (!prioritizedTasks.isEmpty()) {
                        boolean hasOverlap = prioritizedTasks.stream()
                                .anyMatch(subtasks1 -> isTimeOverlap(subtasks1, subtask));
                        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                            if (!hasOverlap) {
                                prioritizedTasks.add(subtask);
                            }
                        } else {
                            prioritizedTasks.add(subtask);
                        }
                        prioritizedTasks.remove(epic);
                        updateEpicTime(epic);
                        prioritizedTasks.add(epic);
                    }
                    updateEpicStatus(epic);
                }
            }
        }
    }

    //  ПОЛУЧЕНИЕ СПИСКА ВСЕХ ЗАДАЧ.
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getIdSubtasks().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    //  УДАЛЕНИЕ ВСЕХ ЗАДАЧ.
    @Override
    public void deleteAllTasks() {
        //удаляем задачи из истории
        getAllTasks().forEach(task -> historyManager.remove(task.getId()));
        //удаляем только задачи типа Task.
        prioritizedTasks.removeIf(task -> task instanceof Task);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        //удаляем эпики из истории
        getAllEpics().forEach(epic -> historyManager.remove(epic.getId()));
        //удаляем только задачи типа Epic.
        prioritizedTasks.removeIf(epic -> epic instanceof Epic);
        deleteAllSubtasks();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        //удаляем подзадачи из истории
        getAllSubtasks().forEach(subtask -> historyManager.remove(subtask.getId()));
        if (epics.isEmpty()) {
            getAllEpics().forEach(epic -> {
                epic.getIdSubtasks().clear();
                epic.setTaskStatus(TaskStatus.NEW);
                epic.setStartTime("01.01.0001 00:00");
                epic.setDuration(0);
                epic.setEndTime(null);
            });
            //удаляем только задачи типа Subtask.
            prioritizedTasks.removeIf(subtask -> subtask instanceof Subtask);
        }
        subtasks.clear();
    }

    //  ПОЛУЧЕНИЕ ЗАДАЧИ ПО ID.
    @Override
    public Task getTask(int key) {
        Task task = tasks.get(key);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int key) {
        Epic epic = epics.get(key);
        if (epic != null) {
            historyManager.add(epics.get(key));
        }
        return epics.get(key);
    }

    @Override
    public Subtask getSubtask(int key) {
        Subtask subtask = subtasks.get(key);
        if (subtask != null) {
            historyManager.add(subtasks.get(key));
        }
        return subtasks.get(key);
    }

    //  ОБНОВЛЕНИЕ СОДЕРЖИМОГО КОНКРЕТНОЙ ЗАДАЧИ.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            //удаляем старый экземпляр из коллекции
            prioritizedTasks.remove(task);
            tasks.put(task.getId(), task);
            if (task.getEndTime() != null
                    && task.getStartTime() != null
                    && task.getStartTime().isAfter(task.getEndTime())) {
                prioritizedTasks.add(task); //добовляем обновленный вариант
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            prioritizedTasks.remove(epic);
            epics.put(epic.getId(), epic);
            if (epic.getStartTime() != null
                    && epic.getEndTime() != null
                    && epic.getStartTime().isAfter(epic.getEndTime())
                    && !prioritizedTasks.isEmpty()) {
                boolean hasOverlap = prioritizedTasks.stream()
                        .anyMatch(epic1 -> isTimeOverlap(epic1, epic));
                if (!hasOverlap) {
                    prioritizedTasks.add(epic);
                }
            } else {
                prioritizedTasks.add(epic);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtask);
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null
                    && subtask.getEndTime() != null
                    && subtask.getStartTime().isAfter(subtask.getEndTime())) {
                prioritizedTasks.add(subtask);
            }
            Epic epic = epics.get(subtask.getIdEpic());
            updateEpicTime(epic);
            updateEpicStatus(epic);
        }
    }

    //  УДАЛЕНИЕ КОНКРЕТНОЙ ЗАДАЧИ.
    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.remove(id);
            epic.getIdSubtasks().stream()
                    .map(subtasks::get)
                    .forEach(subtask -> {
                        subtasks.remove(subtask.getId());
                        historyManager.remove(subtask.getId());
                        prioritizedTasks.remove(subtask);
                    });
            prioritizedTasks.remove(epic);
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            epic.deleteIdSubtasks(id);
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            //обновление состояния эпика
            updateEpicTime(epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}