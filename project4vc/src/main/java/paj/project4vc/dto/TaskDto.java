package paj.project4vc.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import paj.project4vc.enums.TaskPriority;
import paj.project4vc.enums.TaskState;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;


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
    private LocalDate startDate;
    @XmlElement
    private LocalDate endDate;
    @XmlElement
    private boolean deleted;
    @XmlElement
    private String category;

    public TaskDto() {
    }

    public TaskDto(int id, String title, String description, TaskState state, TaskPriority priority, LocalDate startDate, LocalDate endDate, boolean deleted, String category) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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