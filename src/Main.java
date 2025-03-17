import kanban.managers.TaskManager;
import kanban.tasks.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager planner = new TaskManager();

        System.out.println("Phase A: Adding tasks:");
        planner.addTask(new Task("Task A", "Description of task A"));
        planner.addTask(new Task("Task B", "Description of task B"));
        planner.addEpic(new Epic("Epic A", "Description of task A"));
        planner.addSub(new SubTask("Subtask A of Epic A", "Description of Subtask A"), planner.getEpicByID(planner.getGlobalID()));
        planner.addSub(new SubTask("Subtask B of Epic A", "Description of Subtask B"), planner.getEpicByID(planner.getGlobalID() - 1));
        planner.addEpic(new Epic("Epic B", "Description of task B"));
        planner.addSub(new SubTask("Subtask A of Epic B", "Description of Subtask A"), planner.getEpicByID(planner.getGlobalID()));
        showTaskList(planner.getTaskList());
        showEpicList(planner.getEpicList());

        System.out.println("Phase B: Change status of task");
        planner.setTaskStatus(planner.getTaskByID(2), TaskStatus.IN_PROGRESS);
        planner.setSubsStatus(planner.getSubTaskByID(5), TaskStatus.IN_PROGRESS);
        planner.setSubsStatus(planner.getSubTaskByID(7), TaskStatus.DONE);
        showTaskList(planner.getTaskList());
        showEpicList(planner.getEpicList());

        System.out.println("Phase C: Remove task");
        planner.removeTaskByID(planner.getTaskByID(2));
        planner.removeSubsByID(planner.getSubTaskByID(5));
        planner.removeEpicByID(planner.getEpicByID(6));
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
