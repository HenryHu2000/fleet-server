package uk.ac.ic.doc.fleet.entity;

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
    @Column(name = "status")
    private Status status = Status.CREATED;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Task supertask;
    @OneToMany(mappedBy = "supertask")
    private List<Task> subtasks;
    @ManyToOne
    private User user;
    @ManyToOne
    private Device device;

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
    @Column(name = "round")
    private Integer round;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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
