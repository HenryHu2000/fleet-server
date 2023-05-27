package uk.ac.ic.doc.fltee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fltee.entity.Task;

import java.util.UUID;

public interface TaskDao extends JpaRepository<Task, UUID> {

}
