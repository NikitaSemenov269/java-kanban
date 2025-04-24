package managers;

public class DoublyLinkedList<T> {
    public Node<T> head;
    public Node<T> tail;
}

class Node <T> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}




