public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, String description, Status status, int idEpic) {
        super(nameTask, description, status);
        this.idEpic = idEpic;
    }
    public int getIdEpic() {
        return idEpic;
    }
}




