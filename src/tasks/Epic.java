package tasks;

import java.util.*;

public class Epic extends Task {
    private List<Integer> idSubtasks = new ArrayList<>();

    public Epic(String nameEpic, String description) {
        super(nameEpic, description, "01.01.0001 00:00", 0);
    }

    public List<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public boolean isAddIdSubtasks(int id) {
        if (!idSubtasks.contains(id)) {
            this.idSubtasks.add(id);
            return true;
        }
        return false;
    }

    public void deleteIdSubtasks(int id) {
        if (!this.idSubtasks.isEmpty()) {
            idSubtasks.remove(Integer.valueOf(id));
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getTaskStatus() +
                ", idSubtasks=" + getIdSubtasks() +
                ", startTime=" + getStartTimeStr() +
                ", duration=" + getDurationInt() +
                ", endTime=" + getEndTimeStr() +
                '}';
    }
}