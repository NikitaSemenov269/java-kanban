package managers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.*;
import tasks.enums.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    protected void setGenerateId(int maxId) {   // срабатывает при восстановлении данных из файла.
        this.id = maxId;
    }

    //  ГЕНЕРАЦИЯ ID.
    private int generateId() {
        return ++id;
    }

    //  ОБНОВЛЕНИЕ СТАТУСА КОНКРЕТНОГО EPICа.
    private void updateTaskStatus(Epic epic) {
        List<TaskStatus> flags = new ArrayList<>();
        for (int key : epic.getIdSubtasks()) {  //получаем значения id из списка конкретного Tasks.tasks.src.javakanban.Epic
            Subtask subtask = subtasks.get(key);  //получаем значения подзадач по ключам.
            flags.add(subtask.getTaskStatus());
        }
        if (flags.contains(TaskStatus.DONE) && flags.contains(TaskStatus.NEW) || flags.contains(TaskStatus.IN_PROGRESS)) {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (flags.contains(TaskStatus.DONE) && !flags.contains(TaskStatus.NEW) && !flags.contains(TaskStatus.IN_PROGRESS)) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    //  CОЗДАНИЕ ЗАДАЧ.
    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.setTaskStatus(TaskStatus.NEW);
    }

    //  СОЗДАНИЕ SUBTASK В КОНКРЕТНОМ EPICe.
    @Override
    public void createSubtasks(Subtask subtask) {
        subtask.setId(generateId());
        if (epics.containsKey(subtask.getIdEpic())) {     //проверка наличия ключа в epics.
            subtasks.put(subtask.getId(), subtask);       //добавление подзадачи в hashmap.
            Epic epic = epics.get(subtask.getIdEpic());  //получаем экземпляр подходящего Tasks.tasks.src.javakanban.Epic.
            epic.addIdSubtasks(subtask.getId());  //добавление id в список id хранящихся в Tasks.tasks.src.javakanban.Epic.
            updateTaskStatus(epic);   //передаем требуемый экземпляр Tasks.tasks.src.javakanban.Epic в метод обновления статуса.
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
        ArrayList<Subtask> subtasksEpics = new ArrayList<>();
        if (epics.containsValue(epic)) {
            for (int key : epic.getIdSubtasks()) {
                subtasksEpics.add(subtasks.get(key));
            }
        }
        return subtasksEpics;
    }

    @Override
//  УДАЛЕНИЕ ВСЕХ ЗАДАЧ.
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        if (!epics.isEmpty()) {
            for (int key : epics.keySet()) {
                Epic epic = epics.get(key);
                epic.setTaskStatus(TaskStatus.NEW);
            }
        }
    }

    //  ПОЛУЧЕНИЕ ЗАДАЧИ ПО ID.
    @Override
    public Task getTask(int key) {
        Task task = tasks.get(key);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int key) {
        historyManager.add(epics.get(key));
        return epics.get(key);
    }

    @Override
    public Subtask getSubtask(int key) {
        historyManager.add(subtasks.get(key));
        return subtasks.get(key);
    }

    //  ОБНОВЛЕНИЕ СОДЕРЖИМОГО КОНКРЕТНОЙ ЗАДАЧИ.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdEpic());  //получаем экземпляр подходящего Tasks.tasks.src.javakanban.Epic.
            updateTaskStatus(epic);
        }
    }

    //  УДАЛЕНИЕ КОНКРЕТНОЙ ЗАДАЧИ.
    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int key : epic.getIdSubtasks()) {
                historyManager.remove(subtasks.get(key).getId());
                subtasks.remove(key);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getIdEpic());
            epic.deleteIdSubtasks(id);
            subtasks.remove(id);
            historyManager.remove(id);
            updateTaskStatus(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}







