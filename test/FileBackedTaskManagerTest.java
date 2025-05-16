import managers.*;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    @Test
    void savingAnEmptyFile() throws IOException {   // автосохранение пустого файла
        File tempFile = File.createTempFile("testFile", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        fileBackedTaskManager.deleteAllTasks(); // вызываем метод save без значений.
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile); // работаем с пустым файлом.
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пуст.");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пуст.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пуст.");
    }

    @Test
    void savingTasksToFile() throws IOException {
        File tempFile = File.createTempFile("testFile", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Test");
        fileBackedTaskManager.createTask(task1); // создаем задачи при помощи переопределенных методов, содержащих save.
        Task task2 = new Task("Task 1", "Test");
        fileBackedTaskManager.createTask(task2);
        Epic epic1 = new Epic("Epic 1", "Test");
        fileBackedTaskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Test", TaskStatus.NEW, epic1.getId());
        fileBackedTaskManager.createSubtasks(subtask1);

        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile); // заполняем мапы менеджера из файла.

        Task newTask1 = taskManager.getTask(1); // создаем задачи для проверки.
        Task newTask2 = taskManager.getTask(2);
        Epic newEpic1 = taskManager.getEpic(3);
        Subtask newSubtask1 = taskManager.getSubtask(4);
        // сравниваем задачи созданные в fileBackedTaskManager с полученными из файла автосохранения.
        assertEquals(fileBackedTaskManager.getTask(task1.getId()), newTask1, "Задачи не совпадают.");
        assertEquals(fileBackedTaskManager.getTask(task2.getId()), newTask2, "Задачи не совпадают.");
        assertEquals(fileBackedTaskManager.getEpic(epic1.getId()), newEpic1, "Эпики не совпадают.");
        assertEquals(fileBackedTaskManager.getSubtask(subtask1.getId()), newSubtask1, "Подзадачи не совпадают.");
    }
}




