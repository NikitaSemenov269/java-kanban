package managers;

     public class Node <T> {
        public T data;
        public managers.Node<T> next;
        public managers.Node<T> prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }