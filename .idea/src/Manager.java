import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

//  ГЕНЕРАЦИЯ ID.
    public int generateId() {
        return ++id;
    }
//  CОЗДАНИЕ ЗАДАЧ.
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
    }
//  СОЗДАНИЕ SUBTASK В КОНКРЕТНОМ EPICe.
    public void createSubtasks(Subtask subtask) {
        subtask.setId(generateId());
        if (epics.containsKey(subtask.getIdEpic())) {     //проверка наличия ключа в epics.
            subtasks.put(subtask.getId(), subtask);       //добавление подзадачи в hashmap.
            Epic epic = epics.get(subtask.getIdEpic());  //получаем экземпляр подходящего Tasks.Epic.
            epic.addIdSubtasks(subtask.getId());  //добавление id в список id хранящихся в Tasks.Epic.
            updateStatus(epic);   //передаем требуемый экземпляр Tasks.Epic в метод обновления статуса.
        }
    }
//  ОБНОВЛЕНИЕ СТАТУСА КОНКРЕТНОГО EPICа.
    private void updateStatus(Epic epic) {
        ArrayList<Status> flags = new ArrayList<>();
        for (int key : epic.getIdSubtasks()) {  //получаем значения id из списка конкретного Tasks.Epic
            Subtask subtask = subtasks.get(key);  //получаем значения подзадач по ключам.
            flags.add(subtask.getStatus());
        }
        if (flags.contains(Status.DONE) && flags.contains(Status.NEW) || flags.contains(Status.IN_PROGRESS)) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (flags.contains(Status.DONE) && !flags.contains(Status.NEW) && !flags.contains(Status.IN_PROGRESS)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.NEW);
        }
    }
//  ПОЛУЧЕНИЕ СПИСКА ВСЕХ ЗАДАЧ.
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }
//  УДАЛЕНИЕ ВСЕХ ЗАДАЧ.
    public void deleteAllTasks() {
        tasks.clear();
    }
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }
    public void deleteAllSubtasks() {
        subtasks.clear();
        if (!epics.isEmpty()) {
            for (int key : epics.keySet()) {
                Epic epic = epics.get(key);
                epic.setStatus(Status.NEW);
            }
        }
    }
//  ПОЛУЧЕНИЕ ЗАДАЧИ ПО ID.
    public Task getTask(int key) {
        return tasks.get(key);
    }
    public Epic getEpic(int key) {
        return epics.get(key);
    }
    public Subtask getSubtask(int key) {
        return subtasks.get(key);
    }
//  ОБНОВЛЕНИЕ СОДЕРЖИМОГО КОНКРЕТНОЙ ЗАДАЧИ.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getIdEpic());  //получаем экземпляр подходящего Tasks.Epic.
        updateStatus(epic);
    }
//  УДАЛЕНИЕ КОНКРЕТНОЙ ЗАДАЧИ.
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int key : epic.getIdSubtasks()) {
            subtasks.remove(key);
        }
        epics.remove(id);
    }
    public void deleteSubtaskById(int id) {
        if(subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getIdEpic());
            epic.deleteIdSubtasks(id);
            updateStatus(epic);

        }
    }
}






