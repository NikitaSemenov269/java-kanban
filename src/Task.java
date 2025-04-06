import java.util.Objects;

public class Task implements TaskManager {
    protected int id;
    protected String nameTask;
    protected String description;
    protected TaskStatus taskStatus;
    protected Task[] taskHistory = new Task[10];
    protected int count = 0;

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }
    @Override
    public void setId(int id) {
        this.id = id;
    }
    @Override
    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    } //нужен для переопределения имени задачи.
    @Override
    public void setDescription(String description) {
        this.description = description;
    } //нужен для переопр. описания.
    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
    @Override
    public int getId() {
        return id;
    }
    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    @Override
    public void setHistory(Task task) {
        if (count <= taskHistory.length) {
            taskHistory[count] = task;
            count++;
        } else {
            for (int i = 0; i < taskHistory.length - 1; i++) {
                taskHistory[i] = taskHistory[i + 1];
            }
            taskHistory[taskHistory.length - 1] = task;
        }
    }
    @Override
    public Task[] getHistory() {
        return taskHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(nameTask, task.nameTask) && Objects.equals(description, task.description) && taskStatus == task.taskStatus;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nameTask, description, taskStatus);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}