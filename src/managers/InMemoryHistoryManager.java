package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> listOfIDTasksInHistory = new HashMap<>();
    private DoublyLinkedList<Task> linkedList = new DoublyLinkedList<>();

    private void linkLast(Task task, int taskId) {
        Node<Task> newNode = new Node<>(task);
        if (listOfIDTasksInHistory.isEmpty()) {
            linkedList.head = newNode;
            linkedList.tail = newNode;
        } else {
            linkedList.tail.next = newNode;
            newNode.prev = linkedList.tail;
            linkedList.tail = newNode;
            linkedList.tail.next = null;
        }
        listOfIDTasksInHistory.put(taskId, newNode);
    }

    private List<Task> getTasks(DoublyLinkedList<Task> resultingLinkedList) {
        List<Task> taskHistory = new ArrayList<>();
        Node<Task> currentNode = resultingLinkedList.head;
        while (currentNode != null) {
            taskHistory.add(currentNode.data);
            currentNode = currentNode.next;
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
            linkedList.head = taskNode.next;
        }
        if (taskNode.next != null) {
            taskNode.next.prev = taskNode.prev;
        } else {
            linkedList.tail = taskNode.prev;
        }
        if (taskNode.prev == null && taskNode.next == null) {
            linkedList.head = null;
            linkedList.tail = null;
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
        return getTasks(linkedList);
    }

}

