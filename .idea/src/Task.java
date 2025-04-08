import java.util.Objects;

public class Task {
    private int id;
    private String nameTask;
    private String description;
    private Status status;

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
    }
    public Task(String nameTask, String description, Status status) {
        this.nameTask = nameTask;
        this.description = description;
        this.status = status;
    }

     protected void setId(int id) {
        this.id = id;
    }
    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    } //нужен для переопределения имени задачи.
    public void setDescription(String description) {
        this.description = description;
    } //нужен для переопр. описания.
    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(nameTask, task.nameTask) && Objects.equals(description, task.description) && status == task.status;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nameTask, description, status);
    }
    @Override
    public String toString() {
        return "\n" + "Tasks.Task{" +
                "id=" + id +
                ", nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}