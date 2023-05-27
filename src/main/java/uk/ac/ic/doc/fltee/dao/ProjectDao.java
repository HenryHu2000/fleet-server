package uk.ac.ic.doc.fltee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fltee.entity.Project;

public interface ProjectDao extends JpaRepository<Project, Long> {

}
