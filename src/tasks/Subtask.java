package tasks;

import tasks.enums.TaskStatus;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, String description, TaskStatus taskStatus, int idEpic) {
        super(nameTask, description);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + '\'' + getTaskStatus() + '\'' +
                ", idEpic=" + '\'' + getIdEpic() + '\'' +
                '}';
    }
}




