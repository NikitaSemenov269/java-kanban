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
    void testMoreTips() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description",
                TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(subtask);

        assertEquals(taskManager.getTask(task.getId()), task, "Задачи не совпадают_1.");
        assertEquals(taskManager.getEpic(epic.getId()), epic, "Задачи не совпадают_2.");
        assertEquals(taskManager.getSubtask(subtask.getId()), subtask, "Задачи не совпадают_3.");
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
        assertEquals(1, epic.getIdSubtasks().size(), "Количество подзадач не равно 1.");
    }

    @Test
    void noEmptySubtaskIdentifier() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(sub1);
        epic.addIdSubtasks(sub1.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc2", TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(sub2);
        epic.addIdSubtasks(sub2.getId());
        Subtask sub3 = new Subtask("Sub3", "Desc3", TaskStatus.NEW, epic.getId());
        taskManager.createSubtasks(sub3);
        epic.addIdSubtasks(sub3.getId());

        assertEquals(3, epic.getIdSubtasks().size(), "Количество id подзадач не равно 3.");
        assertEquals(epic.getId(), sub1.getIdEpic(), "id эпиков не совпадает_1");
        assertEquals(epic.getId(), sub2.getIdEpic(), "id эпиков не совпадает_2");
        assertEquals(epic.getId(), sub3.getIdEpic(), "id эпиков не совпадает_3");

        assertEquals(sub1, taskManager.getSubtask(sub1.getId()), "Подзадачи не совпадают_1");
        assertEquals(sub2, taskManager.getSubtask(sub2.getId()), "Подзадачи не совпадают_2");
        assertEquals(sub3, taskManager.getSubtask(sub3.getId()), "Подзадачи не совпадают_3");

        taskManager.deleteSubtaskById(sub2.getId());

        assertEquals(2, epic.getIdSubtasks().size(), "Количество id подзадач не равно 2.");
        assertEquals(epic.getId(), sub1.getIdEpic(), "id эпиков не совпадает_4");
        assertEquals(epic.getId(), sub3.getIdEpic(), "id эпиков не совпадает_5");

        assertEquals(sub1, taskManager.getSubtask(sub1.getId()), "Подзадачи не совпадают_4");
        assertEquals(sub3, taskManager.getSubtask(sub3.getId()), "Подзадачи не совпадают_5");
    }
}




