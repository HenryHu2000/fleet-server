package uk.ac.ic.doc.fleet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fleet.entity.Device;

import java.util.UUID;

public interface DeviceDao extends JpaRepository<Device, UUID> {

}
