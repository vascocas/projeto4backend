package aor.paj.proj3_vc_re_jc.dto;

import aor.paj.proj3_vc_re_jc.enums.TaskState;
import jakarta.xml.bind.annotation.XmlElement;

public class UpdateTaskStateDto {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private int id;
    @XmlElement
    private TaskState state;

    public UpdateTaskStateDto() {
    }

    public UpdateTaskStateDto(int id, TaskState state) {
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