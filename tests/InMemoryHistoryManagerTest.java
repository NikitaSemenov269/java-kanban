import managers.*;
import org.junit.jupiter.api.Test;
import tasks.Task;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void addTaskInHistory() {
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
    void historyStoresIssueVersions() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Desc");
        taskManager.createTask(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "Значение списка истории не равно 1.");
        assertEquals(task, historyManager.getHistory().get(0), "Задачи не совпадают_1.");
        Task updatedTask = new Task("Task_1", "Desc_1");
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);
        historyManager.add(updatedTask);
        assertEquals(2, historyManager.getHistory().size(), "Значение списка истории не равно 2.");
        assertEquals(task, historyManager.getHistory().get(0), "Задачи не совпадают_2.");
        assertEquals(updatedTask, historyManager.getHistory().get(1), "Задачи не совпадают_3.");
    }
}