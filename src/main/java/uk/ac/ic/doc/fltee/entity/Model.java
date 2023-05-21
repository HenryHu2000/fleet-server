package uk.ac.ic.doc.fltee.entity;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
@Table(name = "Model")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Column(columnDefinition="MEDIUMBLOB")
    private byte[] ree;
    @Lob
    @Column(columnDefinition="MEDIUMBLOB")
    private byte[] tee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", ree=" + Arrays.toString(ree) +
                ", tee=" + Arrays.toString(tee) +
                '}';
    }
}
