
public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

//          СОЗДАЕМ ДВЕ ЗАДАЧИ.
        Task ferstTask = new Task("Задача №1.", "Тест программы №1.");
        manager.createTask(ferstTask);
        Task secondTask = new Task("Задача №2.", "Тест программы №2.");
        manager.createTask(secondTask);
//          СОЗДАЕМ ПЕРВЫЙ ЭПИК.
        Epic ferstEpic = new Epic("Эпик №1.", "Тест программы №3.");
        manager.createEpic(ferstEpic);
//          СОЗДАЕМ ДВЕ ПОДЗАДАЧИ ПЕРВОГО ЭПИКА.
        Subtask ferstSubtaskFE = new Subtask("Подзадача №1 Эпика №1.", "Тест программы №4",
                TaskStatus.NEW, ferstEpic.getId());
        manager.createSubtasks(ferstSubtaskFE);
        Subtask secondSubtaskFE = new Subtask("Подзадача №2 Эпика №1.", "Тест программы №5",
                TaskStatus.NEW, ferstEpic.getId());
        manager.createSubtasks(secondSubtaskFE);
//          СОЗДАЕМ ВТОРОЙ ЭПИК.
        Epic secondEpic = new Epic("Эпик №2.", "Тест программы №6");
        manager.createEpic(secondEpic);
//          СОЗДАЕМ ПОДЗАДАЧУ ВТОРОГО ЭПИКА.
        Subtask secondSubtaskSE = new Subtask("Подзадача №1 Эпика №2.", "Тест программы №7",
                TaskStatus.NEW, secondEpic.getId());
        manager.createSubtasks(secondSubtaskSE);
        System.out.println("ПЕЧАТАЕМ СПИСКИ ЭПИКОВ, ЗАДАЧ И ПОДЗАДАЧ.");
        System.out.println(manager.getAllEpics() + "\n");
        System.out.println(manager.getAllTasks() + "\n");
        System.out.println(manager.getAllSubtasks() + "\n");
//           ИЗМЕНЯЕМ СТАТУСЫ СОЗДАННЫХ ОБЪЕКТОВ
        ferstTask.setNameTask("Новая задача №1.");
        ferstTask.setDescription("Новый тест программы №1");
        ferstTask.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(ferstTask);
        secondTask.setNameTask("Новая задача №2.");
        secondTask.setDescription("Новый тест программы №2");
        secondTask.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(secondTask);

        ferstEpic.setNameTask("Новый эпик №1.");
        ferstEpic.setDescription("Новый тест программы №3");
        manager.updateEpic(ferstEpic);

        ferstSubtaskFE.setNameTask("Новая подзадача №1 нового эпика №1.");
        ferstSubtaskFE.setDescription("Новый тест программы №4 ");
        ferstSubtaskFE.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(ferstSubtaskFE);

        secondSubtaskFE.setNameTask("Новая подзадача №2 нового эпика №1.");
        secondSubtaskFE.setDescription("Новый тест программы №5 ");
        secondSubtaskFE.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(secondSubtaskFE);

        secondEpic.setNameTask("Новый эпик №2.");
        secondEpic.setDescription("Новый тест программы №6");
        manager.updateEpic(secondEpic);

        secondSubtaskSE.setNameTask("Новая подзадача №1 нового эпика №2.");
        secondSubtaskSE.setDescription("Новый тест программы №7");
        secondSubtaskSE.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(secondSubtaskSE);

        System.out.println("ПЕЧАТАЕМ ОБНОВЛЕННЫЕ СПИСКИ ЭПИКОВ, ЗАДАЧ И ПОДЗАДАЧ.");
        System.out.println(manager.getAllEpics() + "\n");
        System.out.println(manager.getAllTasks() + "\n");
        System.out.println(manager.getAllSubtasks() + "\n");
//          УДАЛЯЕМ ЗАДАЧИ ИЗ ПРОГРАММЫ.
        manager.deleteTaskById(ferstTask.getId());
        manager.deleteSubtaskById(secondSubtaskFE.getId());
        manager.deleteEpicById(secondEpic.getId());

        System.out.println("ПЕЧАТАЕМ СПИСКИ ЭПИКОВ, ЗАДАЧ И ПОДЗАДАЧ ПОСЛЕ УДАЛЕНИЯ.");
        System.out.println(manager.getAllEpics() + "\n");
        System.out.println(manager.getAllTasks() + "\n");
        System.out.println(manager.getAllSubtasks() + "\n");
    }
}
