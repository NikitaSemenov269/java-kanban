import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> idSubtasks = new ArrayList<>();

    public Epic(String nameEpic, String description) {
        super(nameEpic, description);
    }

    public void addIdSubtasks(int id) {
        this.idSubtasks.add(id);
    }

    public void deleteIdSubtasks(int id) {
       if (!this.idSubtasks.isEmpty()) {
            idSubtasks.remove(Integer.valueOf(id));
        }
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }
}








