import kanban.managers.*;
import kanban.tasks.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Scenario A.");
        // Manager initialization
        TaskManager manager = Managers.getDefault();

        // Create different task type
        Task taskA = new Task("Task A", "Description A");
        Task taskB = new Task("Task B", "Description B");
        Epic epicA = new Epic("Epic A", "Description C");
        Epic epicB = new Epic("Epic B", "Description D");
        SubTask subA = new SubTask("Subtask A", "Description E");
        SubTask subB = new SubTask("Subtask B", "Description F");
        SubTask subC = new SubTask("Subtask C", "Description G");

        // Adding task into manager
        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSub(subC);

        // Checking manager storage maps
        System.out.println("Stage 1: Initialization");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicByID(epicA.getID());
        epicB = manager.getEpicByID(epicB.getID());
        subA = manager.getSubTaskByID(subA.getID());
        subB = manager.getSubTaskByID(subB.getID());
        subC = manager.getSubTaskByID(subC.getID());
        epicA.addSubID(subA.getID());
        epicA.addSubID(subB.getID());
        epicB.addSubID(subC.getID());
        subA.setParentID(epicA.getID());
        subB.setParentID(epicA.getID());
        subC.setParentID(epicB.getID());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);
        manager.updateEpic(epicB);

        // Checking epic<->subtask link
        System.out.println("\nStage 2: Epic<->Sub linking");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        // Change task status then update task
        taskA = manager.getTaskByID(taskA.getID());
        subA = manager.getSubTaskByID(subA.getID());
        subC = manager.getSubTaskByID(subC.getID());
        taskA.setStatus(TaskStatus.IN_PROGRESS);
        subA.setStatus(TaskStatus.IN_PROGRESS);
        subC.setStatus(TaskStatus.DONE);
        manager.updateTask(taskA);
        manager.updateSub(subA);
        manager.updateSub(subC);

        // Checking task status update
        System.out.println("\nStage 3: Change task statuses");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        // Remove some task
        manager.removeTaskByID(taskB.getID());
        manager.removeSubByID(subA.getID());
        manager.removeEpicByID(epicB.getID());

        System.out.println("\nStage 4: Task removing");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);


        System.out.println("\nScenario B.");
        manager = Managers.getDefault();

        // Create different task type
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        epicA = new Epic("Epic A", "Description C");
        epicB = new Epic("Epic B", "Description D");
        subA = new SubTask("Subtask A", "Description E");
        subB = new SubTask("Subtask B", "Description F");
        subC = new SubTask("Subtask C", "Description G");

        // Adding task into manager
        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSub(subC);

        System.out.println("Stage 1: Initialization");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicByID(epicA.getID());
        subA = manager.getSubTaskByID(subA.getID());
        subB = manager.getSubTaskByID(subB.getID());
        subC = manager.getSubTaskByID(subC.getID());
        epicA.addSubID(subA.getID());
        epicA.addSubID(subB.getID());
        epicA.addSubID(subC.getID());
        subA.setParentID(epicA.getID());
        subB.setParentID(epicA.getID());
        subC.setParentID(epicA.getID());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);

        System.out.println("\nStage 2: Epic<->Sub linking");
        for (Task task : manager.getTaskList()) System.out.println(task);
        for (Epic epic : manager.getEpicList()) System.out.println(epic);
        for (SubTask sub : manager.getSubList()) System.out.println(sub);

        // Getting task and check history for duplicate entry
        manager.getEpicByID(epicA.getID());
        manager.getTaskByID(taskA.getID());
        manager.getSubTaskByID(subA.getID());
        manager.getEpicByID(epicB.getID());
        System.out.println("\nStage 3-1: History integrity check");
        for (Task task : manager.getHistoryTask()) System.out.println(task);

        manager.getTaskByID(taskB.getID());
        manager.getSubTaskByID(subB.getID());
        manager.getTaskByID(taskA.getID());
        manager.getEpicByID(epicA.getID());
        System.out.println("\nStage 3-2: History integrity check");
        for (Task task : manager.getHistoryTask()) System.out.println(task);

        manager.getSubTaskByID(subB.getID());
        manager.getSubTaskByID(subB.getID());
        manager.getTaskByID(taskB.getID());
        manager.getSubTaskByID(subC.getID());
        System.out.println("\nStage 3-3: History integrity check");
        for (Task task : manager.getHistoryTask()) System.out.println(task);


        // Removing one task then check history
        manager.removeTaskByID(taskA.getID());
        System.out.println("\nStage 4: Removing taskA, checking history");
        for (Task task : manager.getHistoryTask()) System.out.println(task);

        // Removing epic with 3 subtask then check history
        manager.removeEpicByID(epicA.getID());
        System.out.println("\nStage 5: Removing epicA, checking history");
        for (Task task : manager.getHistoryTask()) System.out.println(task);
    }
}
