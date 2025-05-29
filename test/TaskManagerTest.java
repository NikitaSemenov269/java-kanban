import managers.*;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager taskManager = Managers.getDefault();

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", "12.03.2025 11:00", 35);
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void testMoreTips() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", "25.10.2025 13:34", 40);
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description",
                TaskStatus.NEW, "25.10.2025 14:22", 35, epic.getId());
        taskManager.createSubtasks(subtask);

        assertEquals(taskManager.getTask(task.getId()), task);
        assertEquals(taskManager.getEpic(epic.getId()), epic);
        assertEquals(taskManager.getSubtask(subtask.getId()), subtask);
    }

    @Test
    void testEpicStatusAllNew() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.NEW, "25.10.2025 11:25", 40, epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc2", TaskStatus.NEW, "25.10.2025 12:25", 40, epic.getId());
        taskManager.createSubtasks(sub1);
        taskManager.createSubtasks(sub2);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusAllDone() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.DONE, "25.10.2025 11:25", 40, epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc2", TaskStatus.DONE, "25.10.2025 12:25", 40, epic.getId());
        taskManager.createSubtasks(sub1);
        taskManager.createSubtasks(sub2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusMixed() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.NEW, "25.10.2025 11:25", 40, epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc2", TaskStatus.DONE, "25.10.2025 12:25", 40, epic.getId());
        taskManager.createSubtasks(sub1);
        taskManager.createSubtasks(sub2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.IN_PROGRESS, "25.10.2025 11:25", 40, epic.getId());
        taskManager.createSubtasks(sub1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void testTimeOverlap() {
        Task task1 = new Task("Task1", "Desc1", "25.10.2025 10:00", 60);
        Task task2 = new Task("Task2", "Desc2", "25.10.2025 10:30", 30);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }
}