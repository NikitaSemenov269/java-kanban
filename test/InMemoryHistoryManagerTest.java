import managers.Managers;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private TaskManager taskManager = Managers.getDefault();

    @Test
    void addTaskInHistoryList() {
        Task task = new Task("Task", "Desc", "12.03.2025 11:00", 35);
        taskManager.createTask(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void removeTasksFromTheHistoryList() {
        Task task1 = new Task("Task1", "Desc1", "10.03.2025 11:00", 35);
        Task task2 = new Task("Task2", "Desc2", "19.03.2025 11:00", 35);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
    }

    @Test
    void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }
}