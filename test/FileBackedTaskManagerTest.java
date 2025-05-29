import managers.FileBackedTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    protected void setUp() throws IOException {
        tempFile = File.createTempFile("testFile", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void savingAnEmptyFile() throws IOException {
        taskManager.deleteAllTasks();
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(0, loadedManager.getAllTasks().size());
    }

    @Test
    void savingTasksToFile() throws IOException {
        Task task1 = new Task("Task 1", "Test", "25.10.2025 15:00", 40);
        taskManager.createTask(task1);
        Task task2 = new Task("Task 1", "Test", "26.10.2025 15:40", 50);
        taskManager.createTask(task2);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(task1, loadedManager.getTask(1));
        assertEquals(task2, loadedManager.getTask(2));
    }
}
