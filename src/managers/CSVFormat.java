package managers;

import managers.enums.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;

public class CSVFormat {
    private static int maxId = 0;

    // срабатывает при восстановлении данных из файла.
    public static int getGenerateId() {
        return maxId;
    }

    public static Task fromString(String csvLine) {
        TaskType type;
        String[] value = csvLine.split(",");
        try {
            if (maxId < Integer.parseInt(value[0])) {   // получаем максимальный id задач сохраненных в файл.
                maxId = Integer.parseInt(value[0]);
            }
            getGenerateId();
        } catch (NumberFormatException e) {
            throw new NumberFormatException("***Переданное значение не является целым числом: " + value[0] + ".***");
        }
        try {
            type = TaskType.valueOf(value[1].trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("***Некорректный тип задачи: " + value[1] + ".***");
        }
        switch (type) {
            case TASK:
                Task task = new Task(value[2].trim(), value[4].trim());
                task.setId(Integer.parseInt(value[0].trim()));
                task.setTaskStatus(TaskStatus.valueOf(value[3].trim()));
                return task;
            case EPIC:
                Epic epic = new Epic(value[2].trim(), value[4].trim());
                epic.setId(Integer.parseInt(value[0].trim()));
                epic.setTaskStatus(TaskStatus.valueOf(value[3].trim()));
                if (!(value[6].equals("-1"))) {
                    String[] temporaryId = value[6].split("and");
                    for (String id : temporaryId) {
                        epic.addIdSubtasks(Integer.parseInt(id.trim()));
                    }
                }
                return epic;
            case SUBTASK:
                if (!value[5].isEmpty()) {
                    Subtask subtask = new Subtask(value[2].trim(), value[4].trim(),
                            TaskStatus.valueOf(value[3]), Integer.parseInt(value[5].trim()));
                    subtask.setId(Integer.parseInt(value[0].trim()));
                    return subtask;
                }
                throw new IllegalArgumentException("***idEpic отсутствует***");
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type + ".");
        }
    }
}
