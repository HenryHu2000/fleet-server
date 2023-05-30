package uk.ac.ic.doc.fleet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fleet.entity.Model;

import java.util.UUID;

public interface ModelDao extends JpaRepository<Model, UUID> {

}
