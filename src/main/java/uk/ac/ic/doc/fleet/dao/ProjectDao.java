package uk.ac.ic.doc.fleet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fleet.entity.Project;

public interface ProjectDao extends JpaRepository<Project, Long> {

}
