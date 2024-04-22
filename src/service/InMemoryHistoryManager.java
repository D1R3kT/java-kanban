package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> history= new ArrayList<>();

    @Override
    public void add(Task task) {

        if(history.contains(task)){
            history.remove(task);
        }
        if (history.size() < 10) {

        } else if(history.size() == 10){

            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
