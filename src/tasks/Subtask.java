package tasks;

import tasks.enums.TaskStatus;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameSubtask,
                   String description,
                   TaskStatus taskStatus,
                   String startTime,
                   int duration,
                   int idEpic) {
        super(nameSubtask, description, startTime, duration);
        this.idEpic = idEpic;
        setTaskStatus(taskStatus);
    }

    public int getIdEpic() {
        return this.idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getTaskStatus() +
                ", startTime=" + getStartTimeStr() +
                ", duration=" + getDurationInt() +
                ", endTime=" + getEndTimeStr() +
                '}';
    }
}