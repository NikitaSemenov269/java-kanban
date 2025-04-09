import managers.*;
import tasks.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    @Test
    void testIdConflicts() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Task1", "Desc1");
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTask(task.getId()), "Задачи не совпадают.");
    }

    @Test
    void testUniqueIdGeneration() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        taskManager.createTask(task1); // id = 1
        taskManager.createTask(task2); // id = 2
        assertEquals(task1.getId() + 1, task2.getId());
    }

    //  проверьте, что taskmanager.InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void testMoreTips () {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description",
                TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(subtask);

        assertEquals(taskManager.getTask(task.getId()), task, "Задачи не совпадают.");
        assertEquals(taskManager.getEpic(epic.getId()), epic, "Задачи не совпадают.");
        assertEquals(taskManager.getSubtask(subtask.getId()), subtask, "Задачи не совпадают.");
    }

    @Test
    void canNotAddDuplicateSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic); // Создается эпик и добавляется в менеджер
        Subtask sub = new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(sub); // Создается подзадача и добавляется в менеджер
        epic.addIdSubtasks(sub.getId());
        epic.addIdSubtasks(sub.getId());
        assertEquals(1, epic.getIdSubtasks().size(), "Количество подзадач больше 1."); // epic.getSubtaskIds().size должен быть 1
    }
}




