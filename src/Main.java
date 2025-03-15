import Kanban.Managers.TaskManager;
import Kanban.Tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager planner = new TaskManager();

        System.out.println("===== TEST PHASE A:");
        if (planner.addNewTask(new Task("Simple task A", "Description simple task A."))) {
            System.out.println("Simple task A added.");
        } else {
            System.out.println("Task A adding fail.");
        }

        if (planner.addNewTask(new Task("Simple task B", "Description simple task B."))) {
            System.out.println("Simple task B added.");
        } else {
            System.out.println("Task B adding fail.");
        }

        if (planner.addNewTask(new Epic("Epic task A", "Description epic task A."))) {
            System.out.println("Epic task A added.");
        } else {
            System.out.println("Epic A adding fail.");
        }

        if (planner.addNewTask(3, new SubTask(3, "Epic task A Subtask A", "Description epic task A Subtask A."))) {
            System.out.println("Epic task A Subtask A added.");
        } else {
            System.out.println("Epic task A Subtask A adding fail.");
        }

        if (planner.addNewTask(3, new SubTask(3, "Epic task A Subtask B", "Description epic task A Subtask B."))) {
            System.out.println("Epic task A Subtask B added.");
        } else {
            System.out.println("Epic task A Subtask B adding fail.");
        }

        if (planner.addNewTask(new Epic("Epic task B", "Description epic task B."))) {
            System.out.println("Epic task B added.");
        } else {
            System.out.println("Epic B adding fail.");
        }

        if (planner.addNewTask(6, new SubTask(6, "Epic task B Subtask A", "Description epic task B Subtask A."))) {
            System.out.println("Epic task B Subtask A added.");
        } else {
            System.out.println("Epic task B Subtask A adding fail.");
        }

        System.out.println("===== TaskByType:");
        System.out.println("\nTask:");
        System.out.println(planner.getTaskListByType(TaskType.TASK));
        System.out.println("Epic:");
        System.out.println(planner.getTaskListByType(TaskType.EPIC));
        System.out.println("Subtask:");
        System.out.println(planner.getTaskListByType(TaskType.SUB));
        System.out.println("=".repeat(20));
        System.out.println();
        planner.taskListView();

        System.out.println("===== TEST PHASE B:");
        System.out.println("Changing status in Task A:");
        if (planner.updateTaskStatusByID(1, TaskStatus.IN_PROGRESS)) {
            System.out.println("Task A status changed.");
        } else {
            System.out.println("Task A status change fail.");
        }

        System.out.println("Update data in Task B.");
        if (planner.updateTaskByID(2,
                new Task("Update Task B.", "Update description Task B"))) {
            System.out.println("Task B data updated.");
        } else {
            System.out.println("Task B data update fail.");
        }

        System.out.println("Changing status in Task B:");
        if (planner.updateTaskStatusByID(2, TaskStatus.DONE)) {
            System.out.println("Task B status changed.");
        } else {
            System.out.println("Task B status change fail.");
        }

        System.out.println("Update data in Epic task A: Subtask A:");
        if (planner.updateTaskByID(4,
                new SubTask("Update Epic task A Subtask A", "Update description epic task A Subtask A."))) {
            System.out.println("Epic task A: Subtask A data updated.");
        } else {
            System.out.println("Epic task A: Subtask A data update fail.");
        }

        System.out.println("Changing status in Epic task A: Subtask A:");
        if (planner.updateTaskStatusByID(4, TaskStatus.IN_PROGRESS)) {
            System.out.println("Epic task A: Subtask A status changed.");
        } else {
            System.out.println("Epic task A: Subtask A status change fail.");
        }

        System.out.println("Changing status in Epic task B: Subtask A:");
        if (planner.updateTaskStatusByID(7, TaskStatus.DONE)) {
            System.out.println("Epic task B: Subtask A status changed.");
        } else {
            System.out.println("Epic task B: Subtask A status change fail.");
        }

        System.out.println("Update status Epic task A.");
        if (planner.updateTaskStatusByID(3)) {
            System.out.println("Epic task A status updated.");
        } else {
            System.out.println("Epic task A status update fail.");
        }

        System.out.println("Update status Epic task B.");
        if (planner.updateTaskStatusByID(6)) {
            System.out.println("Epic task B status updated.");
        } else {
            System.out.println("Epic task B status update fail.");
        }
        System.out.println();
        planner.taskListView();

        System.out.println("===== TEST PHASE C:");
        System.out.println("Remove Task A.");
        if (planner.removeTaskByID(1)) {
            System.out.println("Task A removed.");
        } else {
            System.out.println("Task A remove fail.");
        }

        System.out.println("Remove Epic task B.");
        if (planner.removeTaskByID(6)) {
            System.out.println("Epic task B removed.");
        } else {
            System.out.println("Epic task B remove fail.");
        }

        System.out.println("Remove Epic task A Subtask A.");
        if (planner.removeTaskByID(4)) {
            System.out.println("Epic task A Subtask A. removed.");
        } else {
            System.out.println("Epic task A Subtask A. remove fail.");
        }
        System.out.println("Update status Epic task A.");
        if (planner.updateTaskStatusByID(3)) {
            System.out.println("Epic task A status updated.");
        } else {
            System.out.println("Epic task A status update fail.");
        }

        System.out.println("\n===== AllTask:");
        System.out.println(planner.getAllTaskIDList());
        System.out.println("\n===== TaskByType:");
        System.out.println("Task:");
        System.out.println(planner.getTaskListByType(TaskType.TASK));
        System.out.println("Epic:");
        System.out.println(planner.getTaskListByType(TaskType.EPIC));
        System.out.println("Subtask:");
        System.out.println(planner.getTaskListByType(TaskType.SUB));
        System.out.println("=".repeat(20));
        System.out.println();
        planner.taskListView();


    }
}
