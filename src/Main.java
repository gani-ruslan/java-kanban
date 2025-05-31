import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;

import java.io.File;
import java.io.IOException;
import kanban.managers.FileBackedTaskManager;
import kanban.managers.InMemoryHistoryManager;
import kanban.managers.Managers;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;

/**
 * The entry point of the Kanban task manager application.
 * This class demonstrates the creation and management of tasks, epics, and subtasks.
 */
public class Main {

    /**
     * Main method to run the application.
     * It creates sample tasks, epics, and subtasks, and demonstrates task history management.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        Task taskA = new Task("Task A", "Description A");
        taskA.setId(1);
        Task taskB = new Task("Task B", "Description B");
        taskB.setId(2);
        Task taskC = new Task("Task C", "Description B");
        taskC.setId(3);

        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);

        System.out.println(historyManager.getTasks());


        System.out.println("FileBackedTaskManager: test scenario A.");
        File tempFile;
        try {
            tempFile = File.createTempFile("_tempCSV", ".csv");
        } catch (IOException e) {
            System.out.println("Can't create temporary file." + e.getMessage());
            return;
        }
        FileBackedTaskManager taskManager = Managers.getFileBackedManager(tempFile);

        // Create different task type
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        Epic epicA = new Epic("Epic A", "Description C");
        Epic epicB = new Epic("Epic B", "Description D");
        SubTask subA = new SubTask("Subtask A", "Description E");
        SubTask subB = new SubTask("Subtask B", "Description F");
        SubTask subC = new SubTask("Subtask C", "Description G");

        // Adding task into manager
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        // Linking epic task and subtask then updating task
        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        epicB = taskManager.getEpicById(epicB.getId()).orElseThrow();
        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subB = taskManager.getSubTaskById(subB.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subC.getId());
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicB.getId());
        taskManager.updateSub(subA);
        taskManager.updateSub(subB);
        taskManager.updateSub(subC);
        taskManager.updateEpic(epicA);
        taskManager.updateEpic(epicB);

        // Change task status then update task
        taskA = taskManager.getTaskById(taskA.getId()).orElseThrow();
        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();
        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);
        taskManager.updateTask(taskA);
        taskManager.updateSub(subA);
        taskManager.updateSub(subC);

        FileBackedTaskManager taskManagerRestored = FileBackedTaskManager.loadFromFile(tempFile);
        tempFile.deleteOnExit();

        System.out.println("Restored task manager:");
        showTask(taskManagerRestored);
        System.out.println("\nOriginal task manager:");
        showTask(taskManager);

        System.out.println("\n\nInMemoryTaskManager: test scenario A.");

        // Create different task type
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        epicA = new Epic("Epic A", "Description C");
        epicB = new Epic("Epic B", "Description D");
        subA = new SubTask("Subtask A", "Description E");
        subB = new SubTask("Subtask B", "Description F");
        subC = new SubTask("Subtask C", "Description G");

        // Manager initialization
        TaskManager manager = Managers.getDefault();

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
        showTask(manager);

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicById(epicA.getId()).orElseThrow();
        epicB = manager.getEpicById(epicB.getId()).orElseThrow();
        subA = manager.getSubTaskById(subA.getId()).orElseThrow();
        subB = manager.getSubTaskById(subB.getId()).orElseThrow();
        subC = manager.getSubTaskById(subC.getId()).orElseThrow();
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
        showTask(manager);

        // Change task status then update task
        taskA = manager.getTaskById(taskA.getId()).orElseThrow();
        subA = manager.getSubTaskById(subA.getId()).orElseThrow();
        subC = manager.getSubTaskById(subC.getId()).orElseThrow();
        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);
        manager.updateTask(taskA);
        manager.updateSub(subA);
        manager.updateSub(subC);

        // Checking task status update
        System.out.println("\nStage 3: Change task statuses");
        showTask(manager);

        // Remove some task
        manager.removeTaskById(taskB.getId());
        manager.removeSubById(subA.getId());
        manager.removeEpicById(epicB.getId());

        System.out.println("\nStage 4: Task removing");
        showTask(manager);

        System.out.println("\n\nInMemoryTaskManager/InMemoryHistoryManager: test scenario B.");
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
        showTask(manager);

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicById(epicA.getId()).orElseThrow();
        subA = manager.getSubTaskById(subA.getId()).orElseThrow();
        subB = manager.getSubTaskById(subB.getId()).orElseThrow();
        subC = manager.getSubTaskById(subC.getId()).orElseThrow();
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
        showTask(manager);

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

    private static void showTask(TaskManager manager) {
        for (Task task : manager.getTaskList()) {
            System.out.println(task.toString());
        }
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic.toString());
            if (!epic.getSubIdList().isEmpty()) {
                for (Integer subTaskId : epic.getSubIdList()) {
                    System.out.println("\t" + manager.getSubTaskById(subTaskId).toString());
                }
            }
        }
    }
}
