
package model;

import java.util.ArrayList;

public class Epic extends Task{

    ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic (String name, String description){
        super(Status.NEW, name, description);
    }

    public Epic (Status status, String name, String description){
        super(status, name, description);
    }

    public ArrayList<SubTask> getSubTasks(){
        return subTasks;
    }

    public void addSubTask(SubTask subTasks){
        this.subTasks.add(subTasks);
    }

    public void removeSubTask(SubTask subTasks){
        this.subTasks.remove(subTasks);
    }

    public ArrayList<SubTask> getSubTask(){
        return subTasks;
    }

}
