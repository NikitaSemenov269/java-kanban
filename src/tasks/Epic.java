package tasks;

import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private List<Integer> idSubtasks;
    private LocalDateTime endTime;

    public Epic(String nameEpic, String description) {
        super(nameEpic, description);
        this.idSubtasks = new ArrayList<>();
    }

    public List<Integer> getIdSubtasks() {
        if (idSubtasks == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(idSubtasks);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAddIdSubtasks(int id) {
        if (idSubtasks == null) {
            idSubtasks = new ArrayList<>();
        }
        if (!idSubtasks.contains(id)) {
            idSubtasks.add(id);
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