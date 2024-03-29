package uk.ac.ic.doc.fleet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import java.util.List;

@Entity
@Table(name = "app_user")
@UserDefinition
public class User implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Username
    @Column(name = "username", unique = true)
    private String username;
    @Password
    @Column(name = "password")
    private String password;
    @Roles
    @Column(name = "role")
    private String role;
    @Column(name = "security_level")
    private Integer securityLevel = 1;
    @Column(name = "score")
    private Double score = 0.0;
    @OneToMany(mappedBy = "user")
    private List<Task> tasks;

    public User() {
    }

    /**
     * Create a new user
     * @param username the username
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role the comma-separated roles
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}