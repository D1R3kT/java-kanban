
package model;

public class SubTask extends Task {

    Epic epic;

    public SubTask(Status status, String task, String description, Epic epic) {
        super(status, task, description);
        this.epic = epic;
    }

    public SubTask(int id,Status status, String task, String description, Epic epic) {
        super(id, status, task, description);
        this.epic = epic;
    }

    public Epic getEpic(){
        return epic;
    }

    public void setEpic(Epic epic){
        this.epic = epic;
    }



}

