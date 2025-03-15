package Kanban.Managers;
import Kanban.Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    // Kanban.Manager.TaskManager fields: base (current edition)
    private static Integer globalTaskID;
    private HashMap<Integer, Object> tasksList;

    // Constructor
    public TaskManager() {
        TaskManager.globalTaskID = 0;
        tasksList = new HashMap<>();
    }


    // Add task
    public boolean addNewTask(Object taskObject) {
        return addTask(taskObject, null);
    }

    public boolean addNewTask(Integer parentEpicTaskID, Object taskObject) {
        return addTask(taskObject, parentEpicTaskID);
    }

    private boolean addTask(Object taskObject, Integer parentTaskID) {
        if (taskObject != null) {
            globalTaskID++;
            switch (TaskType.fromObject(taskObject)) {
                case TASK -> {
                    Task newTask = (Task) taskObject;
                    newTask.setTaskID(globalTaskID);
                    tasksList.put(globalTaskID, newTask);
                    return true;
                }
                case EPIC -> {
                    Epic newTask = (Epic) taskObject;
                    newTask.setTaskID(globalTaskID);
                    tasksList.put(globalTaskID, newTask);
                    return true;
                }
                case SUB -> {
                    if (parentTaskID != null && tasksList.containsKey(parentTaskID)) {
                        SubTask newTask = (SubTask) taskObject;
                        Epic parentTaskUpdate = (Epic) tasksList.get(parentTaskID);
                        newTask.setParentTaskID(parentTaskID);
                        newTask.setTaskID(globalTaskID);
                        parentTaskUpdate.addSubTask(newTask);
                        tasksList.put(globalTaskID, newTask);
                        tasksList.put(parentTaskID, parentTaskUpdate);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // Get tasks ID list
    public ArrayList<Integer> getAllTaskIDList() {
        return new ArrayList<>(tasksList.keySet());
    }

    // Get task list by Type
    public ArrayList<Integer> getTaskListByType(TaskType taskType) {
        ArrayList<Integer> returningList = new ArrayList<>();
        for (Integer taskID : tasksList.keySet()) {
            if (getTypeByTaskID(taskID) == taskType) {
                returningList.add(taskID);
            }
        }
        return returningList;
    }


    // Get task type by task ID
    public TaskType getTypeByTaskID(Integer taskID) {
        return TaskType.fromObject(tasksList.get(taskID));
    }


    // Get task by ID
    public SubTask getSubTaskByID(Integer taskID) {
        return (SubTask) getObjectByID(taskID);
    }

    public Epic getEpicByID(Integer taskID) {
        return (Epic) getObjectByID(taskID);
    }

    public Task getTaskByID(Integer taskID) {
        return (Task) getObjectByID(taskID);
    }

    private Object getObjectByID(Integer taskID) {
        if (tasksList.containsKey(taskID)) {
            return tasksList.get(taskID);
        }
        return null;
    }


    // Remove all tasks
    public void removeAllTask() {
        tasksList.clear();
    }

    // Remove task by ID
    public boolean removeTaskByID(Integer taskID) {
        if (tasksList.containsKey(taskID)) {
            switch (getTypeByTaskID(taskID)) {
                case EPIC -> {
                    Epic removingEpicTask = (Epic) tasksList.get(taskID);
                    for (Integer removingTaskID : removingEpicTask.getSubTaskListIDs()) {
                        tasksList.remove(removingTaskID);
                    }
                }
                case SUB -> {
                    SubTask removingSubTask = (SubTask) tasksList.get(taskID);
                    Epic updatingEpicTask = (Epic) tasksList.get(removingSubTask.getParentTaskID());
                    if (updatingEpicTask.isSubTaskExists(taskID)) {
                        updatingEpicTask.removeSubTask(taskID);
                        tasksList.put(updatingEpicTask.getTaskID(), updatingEpicTask);
                    }
                }
            }
            tasksList.remove(taskID);
            return true;
        }
        return false;
    }


    // Update task by ID
    public boolean updateTaskByID(Integer taskID, Object taskObject) {
        if (taskObject != null && taskID != null && tasksList.containsKey(taskID)) {
            switch (TaskType.fromObject(taskObject)) {
                case TASK -> {
                    Task updatingTask = (Task) taskObject;
                    Task currentTask = (Task) tasksList.get(taskID);
                    currentTask.setTaskName(updatingTask.getTaskName());
                    currentTask.setDescription(updatingTask.getDescription());
                    tasksList.put(taskID, currentTask);
                    return true;
                }
                case EPIC -> {
                    Epic updatingTask = (Epic) taskObject;
                    Epic currentTask = (Epic) tasksList.get(taskID);
                    currentTask.setTaskName(updatingTask.getTaskName());
                    currentTask.setDescription(updatingTask.getDescription());
                    tasksList.put(taskID, currentTask);
                    return true;
                }
                case SUB -> {
                    SubTask updatingTask = (SubTask) taskObject;
                    SubTask currentTask = (SubTask) tasksList.get(taskID);
                    currentTask.setTaskName(updatingTask.getTaskName());
                    currentTask.setDescription(updatingTask.getDescription());
                    tasksList.put(taskID, currentTask);
                    return true;
                }
            }
        }
        return false;
    }


    // Update task status by ID
    public boolean updateTaskStatusByID(Integer taskID) {
        return updateTaskStatus(taskID, TaskStatus.NONE);
    }

    public boolean updateTaskStatusByID(Integer taskID, TaskStatus taskStatus) {
        return updateTaskStatus(taskID, taskStatus);
    }

    private boolean updateTaskStatus(Integer taskID, TaskStatus taskStatus) {
        if (tasksList.containsKey(taskID)) {
            switch (getTypeByTaskID(taskID)) {
                case TASK -> {
                    Task currentTask = (Task) tasksList.get(taskID);
                    currentTask.setTaskStatus(taskStatus);
                    tasksList.put(taskID, currentTask);
                }
                case SUB -> {
                    SubTask currentTask = (SubTask) tasksList.get(taskID);
                    currentTask.setTaskStatus(taskStatus);
                    tasksList.put(taskID, currentTask);
                }
                case EPIC -> {
                    Epic currentTask = (Epic) tasksList.get(taskID);
                    if (!currentTask.isSubTaskListEmpty()) {
                        int subTaskInProgress = 0;
                        int subTaskDone = 0;
                        for (Integer subTaskID : currentTask.getSubTaskListIDs()) {
                            SubTask currentSubTask = (SubTask) tasksList.get(subTaskID);
                            if (currentSubTask.getTaskStatus() == TaskStatus.IN_PROGRESS) { subTaskInProgress++; }
                            if (currentSubTask.getTaskStatus() == TaskStatus.DONE) { subTaskDone++; }
                        }
                        if (subTaskDone > 0 && subTaskDone == currentTask.getSubTaskListIDs().size()) {
                           currentTask.setTaskStatus(TaskStatus.DONE);
                        } else if (subTaskInProgress > 0) {
                            currentTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                        } else {
                            currentTask.setTaskStatus(TaskStatus.NEW);
                        }
                    } else {
                        currentTask.setTaskStatus(TaskStatus.NEW);
                    }
                    tasksList.put(taskID, currentTask);
                }
            }
            return true;
        }
        return false;
    }


    // ====================================================================
    // Debug
    public void taskListView() {
        for (Integer taskID : tasksList.keySet()) {
            switch (getTypeByTaskID(taskID)) {
                case TASK -> {
                    showTask(taskID);
                    System.out.println("=".repeat(20));
                }
                case EPIC -> {
                    showTask(taskID);
                    Epic task = getEpicByID(taskID);
                    if (!task.getSubTaskListIDs().isEmpty()) {
                        for (Integer subTaskID : task.getSubTaskListIDs()) {
                            showTask(subTaskID);
                            System.out.println();
                        }
                    } else {
                        System.out.println("=== SubTask list empty.");
                    }
                    System.out.println("=".repeat(20));
                }
            }

        }
    }

    private void showTask(Integer taskID) {
        switch (getTypeByTaskID(taskID)) {
            case TASK -> {
                Task task = getTaskByID(taskID);
                System.out.println("[SIMPLE] ID: " + taskID + "  Status: " + task.getTaskStatus());
                System.out.println("Title: " + task.getTaskName());
                System.out.println("Description: " + task.getDescription());
            }
            case EPIC -> {
                Epic task = getEpicByID(taskID);
                System.out.println("[EPIC] ID: " + taskID + "  Status: " + task.getTaskStatus());
                System.out.println("Title: " + task.getTaskName());
                System.out.println("Description: " + task.getDescription());
                System.out.println();
            }
            case SUB -> {
                SubTask task = getSubTaskByID(taskID);
                System.out.println("\t[SUB] ID: " + taskID + "  Status: " + task.getTaskStatus());
                System.out.println("\tTitle: " + task.getTaskName());
                System.out.println("\tDescription: " + task.getDescription());
            }
            default -> System.out.println("[X] Unknown task type.");
        }
    }

    // Setters and getters
}
