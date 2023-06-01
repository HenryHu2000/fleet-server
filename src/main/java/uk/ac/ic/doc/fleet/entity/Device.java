package uk.ac.ic.doc.fleet.entity;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "device")
public class Device implements Cloneable {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();
    @Version
    private Long version;
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "security_level")
    private Integer securityLevel;
    @OneToMany(mappedBy = "device")
    private List<Task> tasks;

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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return id.equals(device.id);
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
