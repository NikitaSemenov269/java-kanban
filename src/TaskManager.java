import java.util.ArrayList;

public interface TaskManager {

     void createTask(Task task);
     void createEpic(Epic epic);
     void createSubtasks(Subtask subtask);

     ArrayList<Task> getAllTasks();
     ArrayList<Epic> getAllEpics();
     ArrayList<Subtask> getAllSubtasks();

     void deleteAllTasks();
     void deleteAllEpics();
     void deleteAllSubtasks();

     ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

     Task getTask(int key);
     Epic getEpic(int key);
     Subtask getSubtask(int key);

     void updateTask(Task task);
     void updateEpic(Epic epic);
     void updateSubtask(Subtask subtask);

     void deleteTaskById(int id);
     void deleteEpicById(int id);
     void deleteSubtaskById(int id);

}
