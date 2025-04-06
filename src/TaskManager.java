public interface TaskManager {

     void setId(int id);
     void setNameTask(String nameTask);
     void setDescription(String description);
     void setTaskStatus(TaskStatus taskStatus);
     int getId();
     TaskStatus getTaskStatus();
     void setHistory(Task task);
     Task[] getHistory();
}
