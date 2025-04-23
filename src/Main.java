import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;

import kanban.managers.Managers;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;

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
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        for (SubTask sub : manager.getSubList()) {
            System.out.println(sub);
        }

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicById(epicA.getId());
        epicB = manager.getEpicById(epicB.getId());
        subA = manager.getSubTaskById(subA.getId());
        subB = manager.getSubTaskById(subB.getId());
        subC = manager.getSubTaskById(subC.getId());
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subC.getId());
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicB.getId());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);
        manager.updateEpic(epicB);

        // Checking epic<->subtask link
        System.out.println("\nStage 2: Epic<->Sub linking");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        for (SubTask sub : manager.getSubList()) {
            System.out.println(sub);
        }

        // Change task status then update task
        taskA = manager.getTaskById(taskA.getId());
        subA = manager.getSubTaskById(subA.getId());
        subC = manager.getSubTaskById(subC.getId());
        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);
        manager.updateTask(taskA);
        manager.updateSub(subA);
        manager.updateSub(subC);

        // Checking task status update
        System.out.println("\nStage 3: Change task statuses");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        for (SubTask sub : manager.getSubList()) {
            System.out.println(sub);
        }

        // Remove some task
        manager.removeTaskById(taskB.getId());
        manager.removeSubById(subA.getId());
        manager.removeEpicById(epicB.getId());

        System.out.println("\nStage 4: Task removing");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        for (SubTask sub : manager.getSubList()) {
            System.out.println(sub);
        }


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
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        for (SubTask sub : manager.getSubList()) {
            System.out.println(sub);
        }

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicById(epicA.getId());
        subA = manager.getSubTaskById(subA.getId());
        subB = manager.getSubTaskById(subB.getId());
        subC = manager.getSubTaskById(subC.getId());
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicA.addSubId(subC.getId());
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicA.getId());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);

        System.out.println("\nStage 2: Epic<->Sub linking");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);
        }
        for (SubTask sub : manager.getSubList()) {
            System.out.println(sub);
        }

        // Getting task and check history for duplicate entry
        manager.getEpicById(epicA.getId());
        manager.getTaskById(taskA.getId());
        manager.getSubTaskById(subA.getId());
        manager.getEpicById(epicB.getId());
        System.out.println("\nStage 3-1: History integrity check");
        for (Task task : manager.getHistoryTask()) {
            System.out.println(task);
        }

        manager.getTaskById(taskB.getId());
        manager.getSubTaskById(subB.getId());
        manager.getTaskById(taskA.getId());
        manager.getEpicById(epicA.getId());
        System.out.println("\nStage 3-2: History integrity check");
        for (Task task : manager.getHistoryTask()) {
            System.out.println(task);
        }

        manager.getSubTaskById(subB.getId());
        manager.getSubTaskById(subB.getId());
        manager.getTaskById(taskB.getId());
        manager.getSubTaskById(subC.getId());
        System.out.println("\nStage 3-3: History integrity check");
        for (Task task : manager.getHistoryTask()) {
            System.out.println(task);
        }


        // Removing one task then check history
        manager.removeTaskById(taskA.getId());
        System.out.println("\nStage 4: Removing taskA, checking history");
        for (Task task : manager.getHistoryTask()) {
            System.out.println(task);
        }

        // Removing epic with 3 subtask then check history
        manager.removeEpicById(epicA.getId());
        System.out.println("\nStage 5: Removing epicA, checking history");
        for (Task task : manager.getHistoryTask()) {
            System.out.println(task);
        }
    }
}
