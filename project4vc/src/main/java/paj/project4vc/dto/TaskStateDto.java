package paj.project4vc.dto;

import paj.project4vc.enums.TaskState;
import jakarta.xml.bind.annotation.XmlElement;

public class TaskStateDto {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private int id;
    @XmlElement
    private TaskState state;

    public TaskStateDto() {
    }

    public TaskStateDto(int id, TaskState state) {
        this.id = id;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }
}