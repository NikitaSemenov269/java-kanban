import managers.*;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void addTaskInHistoryList() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Desc");
        taskManager.createTask(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "Значение списка истории не равно 1.");
        assertEquals(task, historyManager.getHistory().get(0), "Задачи не совпадают.");
    }

    //  убедитесь, что задачи, добавляемые в taskmanager.HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void addTasksInHistoryList() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task("Task", "Desc");
        taskManager.createTask(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Значение списка истории не равно 1.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают_1.");

        Task task2 = new Task("Task_1", "Desc_1");
        taskManager.createTask(task2);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size(), "Значение списка истории не равно 2.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают_2.");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи не совпадают_3.");
        assertNotEquals(historyManager.getHistory().get(0), historyManager.getHistory().get(1), "Задачи совпадают_1.");
    }

    @Test
    void updatedTasksInHistoryList() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        Task task3 = new Task("Task3", "Desc3");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "Значение списка истории не равно 3_1.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают_1.");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи не совпадают_2.");
        assertEquals(task3, historyManager.getHistory().get(2), "Задачи не совпадают_3.");

        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);

        assertEquals(3, historyManager.getHistory().size(), "Значение списка истории не равно 3_2.");
        assertEquals(task1, historyManager.getHistory().get(2), "Задачи не совпадают_4.");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи не совпадают_5.");
        assertEquals(task3, historyManager.getHistory().get(0), "Задачи не совпадают_6.");
    }

    @Test
    void removeTasksFromTheHistoryList() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        Task task3 = new Task("Task3", "Desc3");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "Значение списка истории не равно 3_1.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают_1.");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи не совпадают_2.");
        assertEquals(task3, historyManager.getHistory().get(2), "Задачи не совпадают_3.");

        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size(), "Значение списка истории не равно 2_1.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают_4.");
        assertEquals(task3, historyManager.getHistory().get(1), "Задачи не совпадают_5.");
    }
}
