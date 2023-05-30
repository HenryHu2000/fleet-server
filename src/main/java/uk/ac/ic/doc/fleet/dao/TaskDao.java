package uk.ac.ic.doc.fleet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fleet.entity.Task;

import java.util.UUID;

public interface TaskDao extends JpaRepository<Task, UUID> {

}
