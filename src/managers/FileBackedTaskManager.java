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
                Task csvFormat = CSVFormat.fromString(csvLine);

                if (Task.class == csvFormat.getClass()) {
                    manager.tasks.put(csvFormat.getId(), csvFormat);
                    if (!manager.prioritizedTasks.isEmpty()) {
                        if (csvFormat.getStartTime() != null && csvFormat.getEndTime() != null
                                && csvFormat.getEndTime().isAfter(csvFormat.getStartTime())) {
                            manager.prioritizedTasks.add(csvFormat);
                        }
                    } else {
                        manager.prioritizedTasks.add(csvFormat);
                    }
                } else if (Epic.class == csvFormat.getClass()) {
                    manager.epics.put(csvFormat.getId(), (Epic) csvFormat);
                    if (!manager.prioritizedTasks.isEmpty()) {
                        if (csvFormat.getStartTime() != null && csvFormat.getEndTime() != null
                                && csvFormat.getEndTime().isAfter(csvFormat.getStartTime())) {
                            manager.prioritizedTasks.add(csvFormat);
                        }
                    } else {
                        manager.prioritizedTasks.add(csvFormat);
                    }

                } else if (Subtask.class == csvFormat.getClass()) {
                    manager.subtasks.put(csvFormat.getId(), (Subtask) csvFormat);
                    if (csvFormat.getStartTime() != null && csvFormat.getEndTime() != null
                            && csvFormat.getEndTime().isAfter(csvFormat.getStartTime())) {
                        manager.prioritizedTasks.add(csvFormat);
                        Subtask subtask = (Subtask) csvFormat;
                        Epic epic = manager.epics.get(subtask.getIdEpic());
                        if (epic != null) {
                            manager.prioritizedTasks.remove(manager.epics.get(((Subtask) csvFormat).getIdEpic()));
                            manager.updateEpicTime(manager.epics.get(((Subtask) csvFormat)
                                    .getIdEpic()));
                            manager.prioritizedTasks.add(manager.epics
                                    .get(((Subtask) csvFormat).getIdEpic()));
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
                            idSubtasksOfEpic = "-1";
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

//    public static void main(String[] args) {
//        File file = new File("autoSave.csv");
//        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
//        Task task1 = new Task("One", "task");
//        Task task10 = new Task("One", "task");
//        fileBackedTaskManager.createTask(task1);
//        task1.setStartTime("12.05.2024 10:00");
//        fileBackedTaskManager.createTask(task10);
//        task10.setStartTime("17.03.2024 10:00");
//        Task task2 = new Task("Two", "task");
//        fileBackedTaskManager.createTask(task2);
//        Epic epic1 = new Epic("One", "epic");
//        fileBackedTaskManager.createEpic(epic1);
//        Epic epic2 = new Epic("Two", "epic");
//        fileBackedTaskManager.createEpic(epic2);
//        Subtask subtask1 = new Subtask("One", "subtask", TaskStatus.DONE, epic2.getId());
//        fileBackedTaskManager.createSubtasks(subtask1);
//        Subtask subtask2 = new Subtask("Two", "subtask", TaskStatus.NEW, epic1.getId());
//        fileBackedTaskManager.createSubtasks(subtask2);
//        Subtask subtask3 = new Subtask("Two", "subtask", TaskStatus.DONE, epic1.getId());
//        fileBackedTaskManager.createSubtasks(subtask3);
//        Epic epic3 = new Epic("Three ", "epic");
//        fileBackedTaskManager.createEpic(epic3);
//
//        List<Task> allTasks = new ArrayList<>();  // создаем обобщенный список всех задач.
//        allTasks.addAll(fileBackedTaskManager.getAllTasks());
//        allTasks.addAll(fileBackedTaskManager.getAllEpics());
//        allTasks.addAll(fileBackedTaskManager.getAllSubtasks());
//        dataPrinting(allTasks);
//        allTasks.clear();
//        dataPrinting(allTasks);
//
//        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
//        allTasks.addAll(fileBackedTaskManager1.getAllTasks());
//        allTasks.addAll(fileBackedTaskManager1.getAllEpics());
//        allTasks.addAll(fileBackedTaskManager1.getAllSubtasks());
//        dataPrinting(allTasks);
//
//        System.out.println("Список prioritizedTasks:");
//        for (Task task : fileBackedTaskManager1.prioritizedTasks) {
//            System.out.println(task);
//        }
//    }
//
//    private static void dataPrinting(List<Task> allTasks) {  // метод для вывода результатов работы main.
//        if (!allTasks.isEmpty()) {
//            for (Task task : allTasks) {
//                System.out.println(task);
//            }
//        } else {
//            System.out.println("\n***Нет созданных задач.***\n");
//        }
//    }
}