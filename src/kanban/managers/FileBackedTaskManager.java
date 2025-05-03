package kanban.managers;

import static kanban.tasks.TaskStatus.valueOf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import kanban.tasks.TaskStatus;
import kanban.tasks.TaskType;
import kanban.utility.CsvString;

/**
 * FileBackedTaskManager is an implementation of InMemoryTaskManager that adds
 * functionality for persisting tasks to and loading them from a CSV file.
 * It supports all standard task operations and ensures changes are saved to a file automatically.
 * Tasks, epics, and subtasks are serialized into a CSV format for storage.
 * The manager also supports reconstruction of its state from the file.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File taskFile;
    private final CsvString csvString;

    /**
     * Constructs a FileBackedTaskManager with the given file.
     *
     * @param taskFile the file to store tasks
     */
    FileBackedTaskManager(File taskFile) {
        this.taskFile = taskFile;
        csvString = new CsvString("id,type,name,status,description,epic");
    }

    /**
     * Loads a FileBackedTaskManager from the given file if it exists and can be parsed.
     * The method will attempt to reconstruct the state of the task manager from the
     * file's CSV content.
     *
     * @param file the file to load from
     * @return an Optional containing a populated FileBackedTaskManager or empty if loading failed
     */
    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return taskManager;
        }

        int loadedLastId = 0;
        Optional<String> loadedCsvFile = taskManager.loadFile(file);

        if (loadedCsvFile.isPresent()) {

            boolean isParsed = false;

            for (String fileRawString : loadedCsvFile.get().split("\n")) {
                if (fileRawString.equals(taskManager.csvString.getCsvHeader())) {
                    isParsed = true;

                } else if (isParsed && !fileRawString.isBlank()) {
                    Optional<Task> genericTask =
                            taskManager.fromString(fileRawString);

                    if (genericTask.isPresent()) {
                        Task task = genericTask.get();
                        if (task instanceof Epic epic) {
                            loadedLastId = Integer.max(epic.getId(), loadedLastId);
                            taskManager.addEpic(epic);
                        } else if (task instanceof SubTask sub) {
                            loadedLastId = Integer.max(sub.getId(), loadedLastId);
                            taskManager.addSub(sub);
                        } else {
                            loadedLastId = Integer.max(task.getId(), loadedLastId);
                            taskManager.addTask(task);
                        }
                    }
                }
            }

            if (isParsed) {
                taskManager.globalIdCounter = loadedLastId;

                for (SubTask subTask : taskManager.getSubList()) {
                    if (subTask.getParentId() == 0) {
                        continue;
                    }
                    taskManager.getEpicById(
                            subTask.getParentId()).addSubId(subTask.getId()
                    );
                }

                for (Epic epic : taskManager.getEpicList()) {
                    taskManager.updateEpic(epic);
                }
            }
        }
        return taskManager;
    }

    /**
     * Adds a new task to the manager and saves the state to the file.
     *
     * @param newTask the task to add
     */
    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    /**
     * Updates an existing task and saves the updated state to the file.
     *
     * @param updateTask the task to update
     */
    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    /**
     * Removes a task by its ID and saves the updated state to the file.
     *
     * @param taskId the ID of the task to remove
     */
    @Override
    public void removeTaskById(Integer taskId) {
        super.removeTaskById(taskId);
        save();
    }

    /**
     * Adds a new epic to the manager and saves the state to the file.
     *
     * @param newEpic the epic to add
     */
    @Override
    public void addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
    }

    /**
     * Updates an existing epic and saves the updated state to the file.
     *
     * @param updateEpic the epic to update
     */
    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    /**
     * Removes an epic by its ID and saves the updated state to the file.
     *
     * @param epicId the ID of the epic to remove
     */
    @Override
    public void removeEpicById(Integer epicId) {
        super.removeEpicById(epicId);
        save();
    }

    /**
     * Adds a new subtask to the manager and saves the state to the file.
     *
     * @param newSub the subtask to add
     */
    @Override
    public void addSub(SubTask newSub) {
        super.addSub(newSub);
        save();
    }

    /**
     * Updates an existing subtask and saves the updated state to the file.
     *
     * @param updateSub the subtask to update
     */
    @Override
    public void updateSub(SubTask updateSub) {
        super.updateSub(updateSub);
        save();
    }

    /**
     * Removes a subtask by its ID and saves the updated state to the file.
     *
     * @param subId the ID of the subtask to remove
     */
    @Override
    public void removeSubById(Integer subId) {
        super.removeSubById(subId);
        save();
    }

    /**
     * Removes all tasks and saves the updated state to the file.
     */
    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    /**
     * Removes all epics and saves the updated state to the file.
     */
    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    /**
     * Removes all subtasks and saves the updated state to the file.
     */
    @Override
    public void removeAllSub() {
        super.removeAllSub();
        save();
    }

    /**
     * Parses a CSV string into a Task object.
     *
     * @param parseString the CSV string to parse
     * @return an Optional containing the task or empty if parsing failed
     */
    private Optional<Task> fromString(String parseString) {
        Optional<Map<String, String>> parsedData = parseTaskFromCsv(
                parseString, csvString.getCsvHeaderMap()
        );
        if (parsedData.isPresent()) {
            Optional<Task> task = createTaskFromMap(parsedData.get());
            if (task.isPresent()) {
                return task;
            }
        }
        return Optional.empty();
    }

    /**
     * Serializes a Task into a CSV string.
     *
     * @param task the task to serialize
     * @return an Optional containing the serialized task string or empty if serialization failed
     */
    private Optional<String> toString(Task task) {
        return composeTaskToCsv(createMapFromTask(task), csvString.getCsvHeaderMap());
    }

    /**
     * Saves all tasks to the file in CSV format.
     */
    private void save() {
        List<Map<Integer, ? extends Task>> allTask = List.of(epicStorageMap,
                taskStorageMap, subStorageMap);

        StringBuilder tasksData = new StringBuilder(csvString.getCsvHeader() + "\n");
        for (Map<Integer, ? extends Task> tasksMap : allTask) {
            for (Task task : tasksMap.values()) {
                Optional<String> composedCsvTaskString = toString(task);
                composedCsvTaskString.ifPresent(s -> tasksData.append(s).append("\n"));
            }
        }
        saveFile(taskFile, tasksData.toString());
    }

    /**
     * Converts a Task object into a map of field names to values.
     *
     * @param task the task to convert
     * @return an Optional containing the map or empty if the task is null
     */
    private Map<String, String> createMapFromTask(Task task) {

        Map<String, String> taskMap = new HashMap<>();

        // Generic part of all task types
        taskMap.put("id", task.getId().toString());
        taskMap.put("type", task.getType().toString());
        taskMap.put("name", task.getTitle());
        taskMap.put("status", task.getStatus().toString());
        taskMap.put("description", task.getDescription());
        taskMap.put("epic", "");

        // Adding details from different task types
        if (task instanceof SubTask sub) {
            if (sub.getParentId() == null) {
                taskMap.put("epic", "0");
            } else {
                taskMap.put("epic", sub.getParentId().toString());
            }
        }
        return taskMap;
    }

    /**
     * Reconstructs a Task from a field-value map.
     *
     * @param taskMap the map representing the task
     * @return an Optional containing the task or empty if validation failed
     */
    private Optional<Task> createTaskFromMap(Map<String, String> taskMap) {

        ValidData validData;
        Optional<ValidData> validateData = validateValues(taskMap);

        if (validateData.isPresent()) {
            validData = validateData.get();
        } else {
            return Optional.empty();
        }

        switch (validData.type) {
            case TASK -> {
                return Optional.of(new Task(validData.id, validData.name, validData.status,
                        validData.description));
            }
            case SUB -> {
                return Optional.of(new SubTask(validData.id, validData.name, validData.status,
                        validData.description, validData.epic));
            }
            case EPIC -> {
                return Optional.of(new Epic(validData.id, validData.name, validData.status,
                        validData.description));
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    /**
     * Internal record class to store validated and typed task data.
     *
     * @param id task id
     * @param name task name
     * @param status task status
     * @param description task description
     * @param type task type
     * @param epic parent epic id (for subtasks)
     */
    private record ValidData(Integer id, String name, TaskStatus status,
                             String description, TaskType type, Integer epic) {
    }

    /**
     * Validates and parses raw values from a task map into typed task fields.
     * This method ensures that the raw string values in the task map are converted to appropriate
     * types (e.g., integers for IDs, enumerated types for status and type) and checks for validity.
     * If any value is invalid or cannot be parsed, it returns an empty Optional.
     *
     * @param taskMap a map of field names to string values representing a task
     * @return an Optional containing ValidData, which holds the validated and parsed fields,
     *         or empty if any value is invalid or missing
     */
    private Optional<ValidData> validateValues(Map<String, String> taskMap) {
        try {
            // Parse task fields from the map
            int id = Integer.parseInt(taskMap.getOrDefault("id", ""));
            int epic = parseEpicInt(taskMap.get("epic")).orElse(0);
            String name = taskMap.getOrDefault("name", "");
            String description = taskMap.getOrDefault("description", "");
            TaskStatus status = valueOf(taskMap.getOrDefault("status", ""));
            TaskType type = TaskType.valueOf(taskMap.getOrDefault("type", ""));

            // Return the validated data in an Optional
            return Optional.of(new ValidData(id, name, status, description, type, epic));

        } catch (IllegalArgumentException | NullPointerException e) {
            // If any parsing or validation fails, return an empty Optional
            return Optional.empty();
        }
    }

    /**
     * Attempts to parse the "epic" field from a string into an integer.
     * If the value is a valid integer, it returns it; otherwise, it returns an empty Optional.
     *
     * @param value the string value representing the epic ID
     * @return an Optional containing the parsed integer if the value is valid,
     *         or an empty Optional if the value cannot be parsed as an integer
     */
    private Optional<Integer> parseEpicInt(String value) {
        try {
            // Try to parse the epic ID as an integer
            return value != null ? Optional.of(Integer.parseInt(value)) : Optional.empty();
        } catch (NumberFormatException e) {
            // If the value cannot be parsed as an integer, return an empty Optional
            return Optional.empty();
        }
    }

    /**
     * Parses a CSV-formatted string into a map of field names to values.
     *
     * @param parseCsvString the CSV string
     * @param headerCsvMap the header pattern used to interpret field order
     * @return an Optional containing the parsed map or empty if parsing failed
     */
    public Optional<Map<String, String>> parseTaskFromCsv(String parseCsvString,
                                                          Map<Integer, String> headerCsvMap) {
        if (parseCsvString.isBlank()) {
            return Optional.empty();
        }

        Optional<List<String>> parsedCsvString = csvString.parseCsv(parseCsvString);

        Map<String, String> parsedTaskData = new HashMap<>();
        List<String> parsedTaskEntries;

        if (parsedCsvString.isPresent()) {
            parsedTaskEntries = parsedCsvString.get();
            for (int i = 0; i < parsedTaskEntries.size(); i++) {
                parsedTaskData.put(headerCsvMap.get(i), parsedTaskEntries.get(i));
            }
            if (!parsedTaskData.isEmpty()) {
                return Optional.of(parsedTaskData);
            }
        }

        return Optional.empty();
    }

    /**
     * Composes a CSV-formatted string from a map using a predefined header pattern.
     *
     * @param taskMap a map of field names to values
     * @param csvHeaderMap a header map that defines field order
     * @return an Optional containing the composed CSV string or empty if composition failed
     */
    private Optional<String> composeTaskToCsv(Map<String, String> taskMap,
                                              Map<Integer, String> csvHeaderMap) {

        StringBuilder composeString = new StringBuilder();
        Map<Integer, String> orderedPattern = new TreeMap<>(csvHeaderMap);
        for (Map.Entry<Integer, String> entry : orderedPattern.entrySet()) {
            composeString.append(csvString.toCsvEntry(taskMap.get(entry.getValue()))).append(",");
        }

        if (!composeString.isEmpty()) {
            if (composeString.charAt(composeString.length() - 1) == ',') {
                return Optional.of(composeString
                        .deleteCharAt(composeString.length() - 1)
                        .toString());
            }
        }

        return Optional.empty();
    }

    /**
     * Writes the given task data to the specified file. If writing fails,
     * throws a ManagerSaveException.
     *
     * @param savingFile   the file to write to
     * @param taskFileData the string data representing the task list in CSV format
     * @throws ManagerSaveException if the file cannot be written
     */
    private void saveFile(File savingFile, String taskFileData) {
        try {
            Files.writeString(savingFile.toPath(), taskFileData);
        } catch (IOException e) {
            throw new ManagerSaveException("Cannot save taskFile. Error: " + e.getMessage());
        }
    }

    /**
     * Loads the content of the specified file as a string. If the file does not exist
     * or is unreadable, returns an empty Optional.
     *
     * @param loadingFile the file to read from
     * @return an Optional containing the loaded string data, or empty
     * @throws ManagerSaveException if reading the file fails
     */
    private Optional<String> loadFile(File loadingFile) {

        if (!loadingFile.exists() || !loadingFile.isFile()) {
            return Optional.empty();
        }

        String loadedRawData;
        try {
            loadedRawData = Files.readString(loadingFile.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Cannot read taskFile. Error: " + e.getMessage());
        }

        if (loadedRawData.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(loadedRawData);
    }
}
