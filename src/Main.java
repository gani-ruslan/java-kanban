import kanban.managers.InMemoryTaskManager;
import kanban.managers.Managers;
import kanban.managers.TaskManager;
import kanban.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task taskA = new Task("Task A", "Description of task A");
        Task taskB = new Task("Task B", "Description of task B");
        Epic epicA = new Epic("Epic A", "Description of task A");
        Epic epicB = new Epic("Epic B", "Description of task B");
        SubTask subA = new SubTask("Subtask A of Epic A", "Description of Subtask A");
        SubTask subB = new SubTask("Subtask B of Epic A", "Description of Subtask B");
        SubTask subC = new SubTask("Subtask A of Epic B", "Description of Subtask A");

        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSub(subC);

        manager.addSubToEpic(subA.getID(), epicA.getID());
        manager.addSubToEpic(subB.getID(), epicA.getID());
        manager.addSubToEpic(subC.getID(), epicB.getID());

        System.out.println("Pass 1: Init.");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        manager.getTaskByID(taskA.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.getSubTaskByID(subA.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.updateStatus(epicA.getID());
        manager.getSubTaskByID(subC.getID()).setStatus(TaskStatus.DONE);
        manager.updateStatus(epicB.getID());

        System.out.println("\nPass 2: Change status");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        manager.removeTaskByID(taskB.getID());
        manager.removeSubByID(subA.getID());
        manager.removeEpicByID(epicB.getID());

        System.out.println("\nPass 3: Remove some task.");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);
    }
}
