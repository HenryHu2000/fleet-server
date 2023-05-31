package uk.ac.ic.doc.fleet.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "project")
public class Project implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified")
    private Date dateModified;
    @OneToMany(mappedBy = "project")
    private List<Task> tasks;
    @Column(name = "round")
    private Integer round = 0;
    @Column(name = "max_rounds")
    private Integer maxRounds;
    @Column(name = "buffer_size")
    private Integer bufferSize;
    @OneToOne
    @JoinColumn(name = "current_model_id", referencedColumnName = "id")
    private Model currentModel;
    @Column(name = "status")
    private Status status = Status.CREATED;
    @Column(name = "min_user_level")
    private Integer minUserLevel = 0;
    @Column(name = "min_device_level")
    private Integer minDeviceLevel = 0;

    public Project() {
      tasks = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Model getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(Model currentModel) {
        this.currentModel = currentModel;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getMinUserLevel() {
        return minUserLevel;
    }

    public void setMinUserLevel(Integer minUserLevel) {
        this.minUserLevel = minUserLevel;
    }

    public Integer getMinDeviceLevel() {
        return minDeviceLevel;
    }

    public void setMinDeviceLevel(Integer minDeviceLevel) {
        this.minDeviceLevel = minDeviceLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
