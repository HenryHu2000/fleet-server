package uk.ac.ic.doc.fleet.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.*;

@Entity
@Table(name = "model")
public class Model {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();
    @Version
    private Long version;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified")
    private Date dateModified;
    @Lob
    @Column(name = "ree", columnDefinition="MEDIUMBLOB")
    private byte[] ree;
    @Lob
    @Column(name = "tee", columnDefinition="MEDIUMBLOB")
    private byte[] tee;
    @ManyToMany
    @JoinTable(
            name = "task_input",
            joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Task> consumerTasks;
    @ManyToMany
    @JoinTable(
            name = "task_output",
            joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Task> producerTasks;

    public Model() {
        consumerTasks = new ArrayList<>();
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

    public byte[] getRee() {
        return ree;
    }

    public void setRee(byte[] ree) {
        this.ree = ree;
    }

    public byte[] getTee() {
        return tee;
    }

    public void setTee(byte[] tee) {
        this.tee = tee;
    }

    public List<Task> getConsumerTasks() {
        return consumerTasks;
    }

    public void setConsumerTasks(List<Task> consumerTasks) {
        this.consumerTasks = consumerTasks;
    }

    public List<Task> getProducerTasks() {
        return producerTasks;
    }

    public void setProducerTasks(List<Task> producerTasks) {
        this.producerTasks = producerTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return id.equals(model.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
