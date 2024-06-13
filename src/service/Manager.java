package service;

public class Manager {
    public static TaskManager getDefaults() {
        return new FileBackedTaskManager(getDefaultHistory());
        //return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
