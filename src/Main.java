import kanban.managers.TaskManager;
import kanban.tasks.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager planner = new TaskManager();

        System.out.println("Phase A: Adding tasks:");
        planner.addTask(new Task("Task A", "Description of task A"));
        planner.addTask(new Task("Task B", "Description of task B"));

        Epic epicTaskA = new Epic("Epic A", "Description of task A");
        planner.addEpic(epicTaskA);
        planner.addSub(new SubTask("Subtask A of Epic A", "Description of Subtask A"), epicTaskA);
        planner.addSub(new SubTask("Subtask B of Epic A", "Description of Subtask B"), epicTaskA);

        Epic epicTaskB = new Epic("Epic B", "Description of task B");
        planner.addEpic(epicTaskB);
        planner.addSub(new SubTask("Subtask A of Epic B", "Description of Subtask A"), epicTaskB);
        showTaskList(planner.getTaskList());
        showEpicList(planner.getEpicList());

        System.out.println("Phase B: Change status of task");
        planner.getTaskByID(2).setStatus(TaskStatus.IN_PROGRESS);
        planner.getSubTaskByID(5).setStatus(TaskStatus.IN_PROGRESS);
        planner.getSubTaskByID(5).getParentTask().updateStatus();
        planner.getSubTaskByID(7).setStatus(TaskStatus.DONE);
        planner.getSubTaskByID(7).getParentTask().updateStatus();
        showTaskList(planner.getTaskList());
        showEpicList(planner.getEpicList());

        System.out.println("Phase B1: Add new subtask in DONE Epic task:");
        planner.addSub(new SubTask("Subtask B1 of Epic B", "Description of Subtask B1"), epicTaskB);
        showTaskList(planner.getTaskList());
        showEpicList(planner.getEpicList());

        System.out.println("Phase C: Remove task");
        planner.removeTaskByID(2);
        planner.removeSubsByID(5);
        planner.removeEpicByID(6);
        showTaskList(planner.getTaskList());
        showEpicList(planner.getEpicList());
    }

    public static void showTaskList(ArrayList<Task> taskList) {
        for (Task task : taskList) showTask(task);
    }

    public static void showEpicList(ArrayList<Epic> epicList) {
        for (Epic epic : epicList) {
            showTask(epic);
            if (!epic.getSubTaskList().isEmpty()) {
                for (SubTask subTask : epic.getSubTaskList()) showTask(subTask);
            } else {
                System.out.println("SUBTASK LIST: EMPTY");
            }
        }
    }

    public static void showTask(Object taskObject) {
        if (taskObject instanceof Epic epicTask) {
            show("EPIC", epicTask.getStatus(), epicTask.getID(),
                    epicTask.getName(), epicTask.getDescription());
        } else if (taskObject instanceof SubTask subsTask) {
            show("SUBS", subsTask.getStatus(), subsTask.getID(),
                    subsTask.getName(), subsTask.getDescription());
        } else if (taskObject instanceof Task simpleTask) {
            show("TASK", simpleTask.getStatus(), simpleTask.getID(),
                    simpleTask.getName(), simpleTask.getDescription());
        }

    }

    private static void show(String taskType, TaskStatus taskStatus, Integer taskID, String taskTitle,
                             String taskDescription) {
        String beforeTaskSpace = "\n".repeat(0);
        String beforeTaskLimiter = "=".repeat(30);
        String taskIndent = "\t".repeat(0);
        String afterTaskLimiter = "".repeat(30);
        String afterTaskSpace = "\n".repeat(0);

        if (taskType.equals("SUBS")) {
            beforeTaskSpace = "\n".repeat(0);
            beforeTaskLimiter = "-".repeat(25);
            taskIndent = "\t".repeat(1);
            afterTaskLimiter = "".repeat(10);
            afterTaskSpace = "\n".repeat(0);
        }
        System.out.println(taskIndent + beforeTaskSpace + beforeTaskLimiter);
        System.out.println(taskIndent + "[" + taskID + "]" + "[" + taskType + "]" + "[" + taskStatus.toString() + "] " + taskTitle);
        //System.out.println(taskIndent + "Description:");
        System.out.println(taskIndent + taskDescription);
        System.out.println(taskIndent + afterTaskLimiter + afterTaskSpace);
    }
}
