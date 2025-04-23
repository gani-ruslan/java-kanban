package kanban.history;

import kanban.tasks.Task;

/**
 * Represents a node in a doubly linked list that stores a task.
 * Each node has references to both the previous and next nodes in the list.
 */
public class Node {

    private Node nextNode;
    private Node previousNode;
    private final Task task;

    /**
     * Constructs a new node with a given previous node, next node, and task.
     *
     * @param previousNode the previous node in the list
     * @param nextNode the next node in the list
     * @param task the task associated with this node
     */
    public Node(Node previousNode, Node nextNode, Task task) {
        this.previousNode = previousNode;
        this.nextNode = nextNode;
        this.task = task;
    }

    /**
     * Returns the next node in the list.
     *
     * @return the next node
     */
    public Node getNext() {
        return nextNode;
    }

    /**
     * Sets the next node in the list.
     *
     * @param nextNode the next node to be set
     */
    public void setNext(Node nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Returns the previous node in the list.
     *
     * @return the previous node
     */
    public Node getPrevious() {
        return previousNode;
    }

    /**
     * Sets the previous node in the list.
     *
     * @param previousNode the previous node to be set
     */
    public void setPrevious(Node previousNode) {
        this.previousNode = previousNode;
    }

    /**
     * Returns the task associated with this node.
     *
     * @return the task stored in this node
     */
    public Task getNodeTask() {
        return task;
    }
}
