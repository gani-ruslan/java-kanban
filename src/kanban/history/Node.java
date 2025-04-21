package kanban.history;

import kanban.tasks.Task;

public class Node {
    private Node nextNode;
    private Node previousNode;
    private final Task task;

    public Node(Node previousNode, Node nextNode, Task task) {
        this.previousNode = previousNode;
        this.nextNode = nextNode;
        this.task = task;
    }

    public Node getNext() {
        return nextNode;
    }

    public void setNext(Node nextNode) {
        this.nextNode = nextNode;
    }

    public Node getPrevious() {
        return previousNode;
    }

    public void setPrevious(Node previousNode) {
        this.previousNode = previousNode;
    }

    public Task getNodeTask() {
        return task;
    }
}
