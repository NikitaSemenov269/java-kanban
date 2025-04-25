import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    //  убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addNewTask() {   //тест на создание задачи и проверку совпадения при одинаковом id
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //      создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер;
    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(task.getId());
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
        assertEquals(history.get(0), savedTask, "Задачи не совпадают.");
//      убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных;
        task = new Task("1_Test addNewTask_1", "1_Test addNewTask description_1");
        taskManager.updateTask(task);
        assertNotEquals(task, history.get(0), "Ошибка: Задачи совпали");

    }

    //    проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
//    проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    void testMoreTips() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description",
                TaskStatus.NEW, epic.getId());

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtasks(subtask);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        final List<Task> history = historyManager.getHistory();

        assertEquals(history.get(2), task, "Задачи не совпадают.");
        assertEquals(history.get(1), epic, "Задачи не совпадают.");
        assertEquals(history.get(0), subtask, "Задачи не совпадают.");
    }

    //  проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    void addTasks() {
        Task taskFerst = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(taskFerst);
        Task taskSecond = new Task("Test addNewTask", "Test addNewTask description");
        taskSecond.setId(5);
        assertNotEquals(taskSecond, taskManager.getTask(taskFerst.getId()));
    }
}


