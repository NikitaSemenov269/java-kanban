package managers;

import managers.enums.TaskType;
import managers.exceptions.ManagerSaveException;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public static void main(String[] args) {
        File file = new File("autoSave.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("One", "task");
        fileBackedTaskManager.createTask(task1);
        Task task2 = new Task("Two", "task");
        fileBackedTaskManager.createTask(task2);
        Epic epic1 = new Epic("One", "epic");
        fileBackedTaskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("One", "subtask", TaskStatus.NEW, epic1.getId());
        fileBackedTaskManager.createSubtasks(subtask1);

        List<Task> allTasks = new ArrayList<>();  // создаем обобщенный список всех задач.
        allTasks.addAll(fileBackedTaskManager.getAllTasks());
        allTasks.addAll(fileBackedTaskManager.getAllEpics());
        allTasks.addAll(fileBackedTaskManager.getAllSubtasks());
        dataPrinting(allTasks);
        allTasks.clear();
        dataPrinting(allTasks);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        allTasks.addAll(fileBackedTaskManager1.getAllTasks());
        allTasks.addAll(fileBackedTaskManager1.getAllEpics());
        allTasks.addAll(fileBackedTaskManager1.getAllSubtasks());
        dataPrinting(allTasks);
    }

    private static void dataPrinting(List<Task> allTasks) {  // метод для вывода результатов работы main.
        if (!allTasks.isEmpty()) {
            for (Task task : allTasks) {
                System.out.println(task);
            }
        } else {
            System.out.println("\n***Нет созданных задач.***\n");
        }
    }

    // далее логика программы.
    private int maxId = 0;
    TaskType type = null;
    private final File autoSave;

    public FileBackedTaskManager(File autoSave) {
        this.autoSave = autoSave;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                String csvLine = fileReader.readLine();
                if (Task.class == manager.fromString(csvLine).getClass()) {
                    manager.tasks.put(manager.fromString(csvLine).getId(), manager.fromString(csvLine));
                } else if (Epic.class == manager.fromString(csvLine).getClass()) {
                    manager.epics.put(manager.fromString(csvLine).getId(), (Epic) manager.fromString(csvLine));
                } else if (Subtask.class == manager.fromString(csvLine).getClass()) {
                    manager.subtasks.put(manager.fromString(csvLine).getId(), (Subtask) manager.fromString(csvLine));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("***Ошибка чтения из файла.***", e);
        }
        return manager;
    }

    private void save() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getAllTasks());
        allTasks.addAll(getAllEpics());
        allTasks.addAll(getAllSubtasks());

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(autoSave))) {
            String csvLine;
            String idEic = "";
            for (Task task : allTasks) {
                if (task == null) {
                    System.out.print("***Передано null значение вместо задачи.***");
                    continue;
                }
                if (task.getClass() == Task.class) {
                    type = TaskType.TASK;
                } else if (task.getClass() == Epic.class) {
                    type = TaskType.EPIC;
                } else if (task.getClass() == Subtask.class) {
                    type = TaskType.SUBTASK;
                    idEic = String.valueOf(((Subtask) task).getIdEpic());
                } else {
                    System.out.println("***Неизвестный тип задачи***");
                    continue;
                }
                csvLine = String.format("%d,%s,%s,%s,%s,%s%n",
                        task.getId(),
                        type,
                        task.getNameTask(),
                        task.getTaskStatus(),
                        task.getDescription(),
                        idEic);

                fileWriter.write(csvLine);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("***Ошибка сохранения в файл: " + autoSave.getName() + ".");
        }
    }

    private Task fromString(String csvLine) {
        String[] value = csvLine.split(",");
        try {
            if (maxId < Integer.parseInt(value[0])) {   // получаем максимальный id задач сохраненных в файл.
                maxId = Integer.parseInt(value[0]);
            }
            setGenerateId(maxId);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("***Переданное значение не является целым числом: " + value[0] + ".***");
        }
        try {
            type = TaskType.valueOf(value[1].trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("***Некорректный тип задачи: " + value[1] + ".***");
        }
        switch (type) {
            case TASK:
                return new Task(Integer.parseInt(value[0].trim()), value[2].trim(), value[4].trim(),
                        TaskStatus.valueOf(value[3].trim()));
            case EPIC:
                return new Epic(Integer.parseInt(value[0].trim()), value[2].trim(), value[4].trim(),
                        TaskStatus.valueOf(value[3].trim()));
            case SUBTASK:
                if (!value[5].isEmpty()) {
                    return new Subtask(Integer.parseInt(value[0].trim()), value[2].trim(), value[4].trim(),
                            TaskStatus.valueOf(value[3]), Integer.parseInt(value[5].trim()));
                }
                throw new IllegalArgumentException("***idEpic отсутствует***");
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type + ".");
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
        return "FileBackedTaskManager{" +
                "tasks=" + tasks +
                ", subtasks=" + subtasks +
                ", epics=" + epics +
                '}';
    }
}
