import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.*;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task ferstTask = new Task("1 задача", "...");
        manager.createTask(ferstTask);
        Task secondTask = new Task("2 задача", "...");
        manager.createTask(secondTask);
        Epic ferstEpic = new Epic("1 эпик", "с подзадачами");
        manager.createEpic(ferstEpic);
        Epic secondEpic = new Epic("2 эпик", "без подзадач");
        manager.createEpic(secondEpic);
        Subtask ferstSubtask = new Subtask("1 подзадача", "...", TaskStatus.NEW, ferstEpic.getId());
        manager.createSubtasks(ferstSubtask);
        Subtask secondSubtask = new Subtask("2 подзадача", "...", TaskStatus.NEW, ferstEpic.getId());
        manager.createSubtasks(secondSubtask);
        Subtask thirdSubtask = new Subtask("3 подзадача", "...", TaskStatus.NEW, ferstEpic.getId());
        manager.createSubtasks(thirdSubtask);

        manager.getTask(secondTask.getId());
        showHistoryWithUnderlining(manager);
        manager.getTask(ferstTask.getId());
        showHistoryWithUnderlining(manager);
        manager.getSubtask(ferstSubtask.getId());
        showHistoryWithUnderlining(manager);
        manager.getSubtask(secondSubtask.getId());
        showHistoryWithUnderlining(manager);
        manager.getSubtask(ferstSubtask.getId());
        showHistoryWithUnderlining(manager);
        manager.getSubtask(thirdSubtask.getId());
        showHistoryWithUnderlining(manager);
        manager.getEpic(secondEpic.getId());
        showHistoryWithUnderlining(manager);
        manager.getTask(secondTask.getId());
        showHistoryWithUnderlining(manager);

        printAllTasks(manager);

        manager.deleteTaskById(ferstTask.getId());
        showHistoryWithUnderlining(manager);
        manager.deleteEpicById(ferstEpic.getId());
        showHistoryWithUnderlining(manager);

        printAllTasks(manager);
    }

    public static void printAllTasks(TaskManager manager) {
        if (!manager.getAllTasks().isEmpty()) {
            System.out.println("Задачи:");
            for (Task task : manager.getAllTasks()) {
                System.out.println(task);
            }
            System.out.println();
        } else {
            System.out.println("***На данный момент нет созданных задач.*** \n");
        }
        if (!manager.getAllEpics().isEmpty()) {
            System.out.println("Эпики:");
            for (Epic epic : manager.getAllEpics()) {
                System.out.println(epic);
                for (Task subtask : manager.getSubtasksOfEpic(epic)) {
                    System.out.println("--> " + subtask);
                }
                System.out.println();
            }
        } else {
            System.out.println("***На данный момент нет созданных эпиков.*** \n");
        }
        if (!manager.getAllSubtasks().isEmpty()) {
            System.out.println("Подзадачи:");
            for (Task subtask : manager.getAllSubtasks()) {
                System.out.println(subtask);
            }
            System.out.println();
        } else {
            System.out.println("***На данный момент нет созданных подзадач.*** \n");
        }
        System.out.println("История:");
        showHistory(manager);
        System.out.println("=".repeat(100));
    }

    private static void showHistory(TaskManager manager) {
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void showHistoryWithUnderlining(TaskManager manager) {
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(100));
    }
}