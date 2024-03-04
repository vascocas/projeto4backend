package aor.paj.proj3_vc_re_jc.dto;

import aor.paj.proj3_vc_re_jc.enums.TaskPriority;
import aor.paj.proj3_vc_re_jc.enums.TaskState;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class TaskDto {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private int id;
    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private TaskState state;
    @XmlElement
    private TaskPriority priority;
    @XmlElement
    private String startDate;
    @XmlElement
    private String endDate;
    @XmlElement
    private boolean deleted;
    @XmlElement
    private String category;

    public TaskDto() {
    }

    public TaskDto(int id, String title, String description, TaskState state, TaskPriority priority, String startDate, String endDate, boolean deleted, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = state;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deleted = deleted;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}