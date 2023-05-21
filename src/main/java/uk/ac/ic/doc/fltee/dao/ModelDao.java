package uk.ac.ic.doc.fltee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fltee.entity.Model;

public interface ModelDao extends JpaRepository<Model, Long> {

}
