package kanban.utility;

import kanban.tasks.Task;

/**
 * Represents a node in a doubly linked list that stores a task.
 * Each node has references to both the previous and next nodes in the list.
 */
public class HistoryNode {

    private HistoryNode nextHistoryNode;
    private HistoryNode previousHistoryNode;
    private final Task task;

    /**
     * Constructs a new node with a given previous node, next node, and task.
     *
     * @param previousHistoryNode the previous node in the list
     * @param nextHistoryNode the next node in the list
     * @param task the task associated with this node
     */
    public HistoryNode(HistoryNode previousHistoryNode, HistoryNode nextHistoryNode, Task task) {
        this.previousHistoryNode = previousHistoryNode;
        this.nextHistoryNode = nextHistoryNode;
        this.task = task;
    }

    /**
     * Returns the next node in the list.
     *
     * @return the next node
     */
    public HistoryNode getNext() {
        return nextHistoryNode;
    }

    /**
     * Sets the next node in the list.
     *
     * @param nextHistoryNode the next node to be set
     */
    public void setNext(HistoryNode nextHistoryNode) {
        this.nextHistoryNode = nextHistoryNode;
    }

    /**
     * Returns the previous node in the list.
     *
     * @return the previous node
     */
    public HistoryNode getPrevious() {
        return previousHistoryNode;
    }

    /**
     * Sets the previous node in the list.
     *
     * @param previousHistoryNode the previous node to be set
     */
    public void setPrevious(HistoryNode previousHistoryNode) {
        this.previousHistoryNode = previousHistoryNode;
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
