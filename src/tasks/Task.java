package tasks;

import tasks.enums.TaskStatus;

import java.util.Objects;

public class Task {
    private int id;
    private String nameTask;
    private String description;
    private TaskStatus taskStatus;

    public Task(String nameTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }

    // Обеспечивает полное сохранение/получение данных в/из файла.
    public Task(int id, String nameTask, String description, TaskStatus taskStatus) {
        this.id = id;
        this.nameTask = nameTask;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    } //нужен для переопределения имени задачи.

    public void setDescription(String description) {
        this.description = description;
    } //нужен для переопр. описания.

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getNameTask() {
        return nameTask;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
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
        return "tasks.src.javakanban.Task{" +
                "id=" + id +
                ", nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}