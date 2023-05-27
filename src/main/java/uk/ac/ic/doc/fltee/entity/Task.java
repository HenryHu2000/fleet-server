package uk.ac.ic.doc.fltee.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.*;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();
    @Version
    private Long version;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified")
    private Date dateModified;
    @Column(name = "task_type")
    private TaskType taskType;
    @Column(name = "task_status")
    private TaskStatus taskStatus = TaskStatus.CREATED;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Task supertask;
    @OneToMany(mappedBy = "supertask")
    private List<Task> subtasks;
    @ManyToMany
    @JoinTable(
            name = "task_input",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "model_id"))
    private List<Model> inputModels;
    @ManyToMany
    @JoinTable(
            name = "task_output",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "model_id"))
    private List<Model> outputModels;

    public Task() {
        subtasks = new ArrayList<>();
        inputModels = new ArrayList<>();
        outputModels = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task getSupertask() {
        return supertask;
    }

    public void setSupertask(Task supertask) {
        this.supertask = supertask;
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Task> subtasks) {
        this.subtasks = subtasks;
    }

    public List<Model> getInputModels() {
        return inputModels;
    }

    public void setInputModels(List<Model> inputModels) {
        this.inputModels = inputModels;
    }

    public List<Model> getOutputModels() {
        return outputModels;
    }

    public void setOutputModels(List<Model> outputModels) {
        this.outputModels = outputModels;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
