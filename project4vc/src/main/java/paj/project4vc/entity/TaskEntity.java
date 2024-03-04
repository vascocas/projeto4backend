package aor.paj.proj3_vc_re_jc.entity;

import aor.paj.proj3_vc_re_jc.enums.TaskPriority;
import aor.paj.proj3_vc_re_jc.enums.TaskState;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "task")
@NamedQuery(name = "Task.findAllActiveTasks", query = "SELECT t FROM TaskEntity t WHERE t.deleted = :deleted")
@NamedQuery(name = "Task.findTaskById", query = "SELECT t FROM TaskEntity t WHERE t.id = :id")
@NamedQuery(name = "Task.findTaskByIdAndUser", query = "SELECT t FROM TaskEntity t WHERE t.id = :id AND t.creator = :creator")
@NamedQuery(name = "Task.findTasksByUser", query = "SELECT t FROM TaskEntity t WHERE t.creator = :creator AND t.deleted = :deleted")
@NamedQuery(name = "Task.findTasksByCategoryId", query = "SELECT t FROM TaskEntity t WHERE t.category.id = :categoryId")
@NamedQuery(name = "Task.findTasksByDeleted", query = "SELECT t FROM TaskEntity t WHERE t.deleted = :deleted")
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "title", nullable = false, unique = false, length = 25)
    private String title;

    @Column(name = "description", nullable = false, unique = false, length = 65535, columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false, unique = false, updatable = true)
    private String startDate;

    @Column(name = "end_date", nullable = true, unique = false, updatable = true)
    private String endDate;

    @Column(name = "state", nullable = false, unique = false, updatable = true)
    private int state;

    @Column(name = "priority", nullable = false, unique = false, updatable = true)
    private int priority;

    @Column(name = "deleted", nullable = false, unique = false, updatable = true)
    private boolean deleted;

    //Owning Side User - Task
    @ManyToOne
    private UserEntity creator;

    //Owning Side Category - Task
    @ManyToOne
    private CategoryEntity category;

    public TaskEntity() {
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

    public TaskState getState() {
        return TaskState.fromValue(this.state);
    }

    public void setState(TaskState state) {
        this.state = state.getValue();
    }

    public TaskPriority getPriority() {
        return TaskPriority.fromValue(this.priority);
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority.getValue();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}