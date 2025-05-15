package managers;

import managers.interfaces.HistoryManager;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> listOfIDTasksInHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task task, int taskId) {
        Node<Task> newNode = new Node<>(task);
        if (listOfIDTasksInHistory.isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
            tail.next = null;
        }
        listOfIDTasksInHistory.put(taskId, newNode);
    }

    private List<Task> getTasks() {
        List<Task> taskHistory = new ArrayList<>();
        Node<Task> currentNode = tail;
        while (currentNode != null) {
            taskHistory.add(currentNode.data);
            currentNode = currentNode.prev;
        }
        return new ArrayList<>(taskHistory);
    }

    private void removeNode(Node<Task> taskNode) {
        if (taskNode == null) {
            return;
        }
        if (taskNode.prev != null) {
            taskNode.prev.next = taskNode.next;
        } else {
            head = taskNode.next;
        }
        if (taskNode.next != null) {
            taskNode.next.prev = taskNode.prev;
        } else {
            tail = taskNode.prev;
        }
        if (taskNode.prev == null && taskNode.next == null) {
            head = null;
            tail = null;
        }
        listOfIDTasksInHistory.remove(taskNode.data.getId());
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if (listOfIDTasksInHistory.containsKey(taskId)) {
            removeNode(listOfIDTasksInHistory.get(taskId));
        }
        linkLast(task, taskId);
    }

    @Override
    public void remove(int id) {
        Node<Task> task = listOfIDTasksInHistory.get(id);
        if (task != null) {
            removeNode(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

}

