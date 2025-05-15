package tasks;

import tasks.enums.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> idSubtasks = new ArrayList<>();

    public Epic(String nameEpic, String description) {
        super(nameEpic, description);
    }

    public Epic(int id, String nameEpic, String description, TaskStatus taskStatus) {
        super(id, nameEpic, description, taskStatus);
    }

    public void addIdSubtasks(int id) {
        if (!idSubtasks.contains(id)) {
            this.idSubtasks.add(id);
        }
    }

    public void deleteIdSubtasks(int id) {
        if (!this.idSubtasks.isEmpty()) {
            idSubtasks.remove(Integer.valueOf(id));
        }
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}