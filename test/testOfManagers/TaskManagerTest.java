package testOfManagers;

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
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void testMoreTips() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description",
                TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(subtask);

        assertEquals(taskManager.getTask(task.getId()), task);
        assertEquals(taskManager.getEpic(epic.getId()), epic);
        assertEquals(taskManager.getSubtask(subtask.getId()), subtask);
    }

    @Test
    void testEpicStatusAllNew() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.NEW, epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(sub1);
        taskManager.createSubtasks(sub2);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void testTimeOverlap() {
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }
}