package tasks;

import tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Objects;

import static managers.FileBackedTaskManager.formatter;

public class Task {
    private int id; // 0
    private String nameTask;  // 1
    private String description; // 2
    private TaskStatus taskStatus; // 3
    private LocalDateTime startTime; // 4
    private Duration duration;//5
    private LocalDateTime endTime;

    public Task(String nameTask, String description, String startTime, int duration) {
        this.nameTask = nameTask;
        this.description = description;
        if (startTime != null && !startTime.isBlank()) {
            this.startTime = LocalDateTime.parse(startTime, formatter);
        }
        this.duration = Duration.ofMinutes(duration);
        this.taskStatus = TaskStatus.NEW;
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

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public void setStartTimeEpic(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(int duration) {
        this.duration = Duration.ofMinutes(duration);
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeStr() {
        return LocalDateTime.now().format(formatter);
    }

    public Duration getDuration() {
        return duration;
    }

    public int getDurationInt() {
        return (int)duration.toMinutes();
    }

    public int getDurationMinutes() {
        return (int) this.duration.toMinutes();
    }

    public LocalDateTime getEndTime() {
        if (duration.toMinutes() < 0) {
            throw new IllegalArgumentException("***Отрицательное значение duration: " + duration);
        }
        return startTime.plus(duration);
    }

    public String getEndTimeStr() {
        return LocalDateTime.now().format(formatter);
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
                ", startTime=" + getStartTimeStr() +
                ", duration=" + getDurationInt() +
                ", endTime=" + getEndTimeStr() +
                '}';
    }
}