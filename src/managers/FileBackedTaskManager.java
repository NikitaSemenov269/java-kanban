package managers;

import managers.enums.TaskType;
import managers.exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File autoSave;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTaskManager(File autoSave) {
        this.autoSave = autoSave;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                String csvLine = fileReader.readLine();
                Task task = CSVFormat.fromString(csvLine);

                if (task instanceof Task) {
                    manager.tasks.put(task.getId(), task);
                    if (!manager.prioritizedTasks.isEmpty()) {
                        if (task.getStartTime() != null && task.getEndTime() != null
                                && task.getEndTime().isAfter(task.getStartTime())) {
                            manager.prioritizedTasks.add(task);
                        }
                    } else {
                        manager.prioritizedTasks.add(task);
                    }
                } else if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    manager.epics.put(epic.getId(), epic);
                    if (!manager.prioritizedTasks.isEmpty()) {
                        if (epic.getStartTime() != null && epic.getEndTime() != null
                                && epic.getEndTime().isAfter(epic.getStartTime())) {
                            manager.prioritizedTasks.add(epic);
                        }
                    } else {
                        manager.prioritizedTasks.add(epic);
                    }

                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    manager.subtasks.put(subtask.getId(), subtask);
                    if (subtask.getStartTime() != null && subtask.getEndTime() != null
                            && subtask.getEndTime().isAfter(subtask.getStartTime())) {
                        manager.prioritizedTasks.add(subtask);
                        Epic epic = manager.epics.get(subtask.getIdEpic());
                        if (epic != null) {
                            manager.prioritizedTasks.remove(manager.epics.get(subtask.getIdEpic()));
                            manager.updateEpicTime(manager.epics.get(subtask.getIdEpic()));
                            manager.prioritizedTasks.add(manager.epics
                                    .get(subtask.getIdEpic()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("***Ошибка чтения из файла.***", e);
        }
        return manager;
    }

    private void save() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(Optional.ofNullable(getAllTasks()).orElse(Collections.emptyList()));
        allTasks.addAll(Optional.ofNullable(getAllEpics()).orElse(Collections.emptyList()));
        allTasks.addAll(Optional.ofNullable(getAllSubtasks()).orElse(Collections.emptyList()));

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(autoSave))) {
            allTasks.stream()
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                        String idEpic = "";
                        String idSubtasksOfEpic = "";
                        TaskType type;

                        if (task.getClass() == Task.class) {
                            type = TaskType.TASK;
                        } else if (task.getClass() == Epic.class) {
                            type = TaskType.EPIC;
                            idSubtasksOfEpic = "";
                            if (!((Epic) task).getIdSubtasks().isEmpty()) {
                                String[] temporaryId = String.valueOf(((Epic) task).getIdSubtasks()).split(", ");
                                idSubtasksOfEpic = String.join("and", temporaryId);
                                idSubtasksOfEpic = idSubtasksOfEpic.replace("[", "");
                                idSubtasksOfEpic = idSubtasksOfEpic.replace("]", "");
                                idSubtasksOfEpic = idSubtasksOfEpic.trim();
                            }
                        } else if (task.getClass() == Subtask.class) {
                            type = TaskType.SUBTASK;
                            idEpic = String.valueOf(((Subtask) task).getIdEpic());
                        } else {
                            System.out.println("***Неизвестный тип задачи***");
                            return;
                        }
                        String csvLine = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d%n",
                                task.getId(), //0
                                type, //1
                                task.getNameTask(), //2
                                task.getTaskStatus(), //3
                                task.getDescription(), //4
                                idEpic, //5
                                idSubtasksOfEpic, //6
                                Optional.ofNullable(task.getStartTime())
                                        .map(time -> time.format(formatter))
                                        .orElse(""), //7
                                task.getDurationMinutes()); //8

                        try {
                            fileWriter.write(csvLine);
                        } catch (IOException e) {
                            throw new ManagerSaveException("***Ошибка сохранения в файл: " + autoSave.getName() + ".");
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("***Ошибка сохранения в файл: " + autoSave.getName() + ".");
        }
    }

    @Override
    public void createSubtasks(Subtask subtask) {
        super.createSubtasks(subtask);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        return super.getSubtasksOfEpic(epic);
    }

    @Override
    public Task getTask(int key) {
        return super.getTask(key);
    }

    @Override
    public Epic getEpic(int key) {
        return super.getEpic(key);
    }

    @Override
    public Subtask getSubtask(int key) {
        return super.getSubtask(key);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateEpicTime(Epic epic) {
        super.updateEpicTime(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public String toString() {
        return "FileBackedTaskManager{" + "tasks=" + tasks + ", subtasks=" + subtasks + ", epics=" + epics + '}';
    }
}