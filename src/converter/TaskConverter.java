package converter;

import model.Task;

public class TaskConverter {


    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + task.getEpicId() + "," + task.getDuration() + "," + task.getStartTime();
    }
}
