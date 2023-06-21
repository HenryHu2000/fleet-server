package uk.ac.ic.doc.fleet.service;

import uk.ac.ic.doc.fleet.entity.Project;
import uk.ac.ic.doc.fleet.entity.Status;

import java.util.Optional;

public interface IProjectService {
    Optional<Project> getProjectOverview(Long id) throws CloneNotSupportedException;
    Optional<Status> setProjectStatus(Long id, Status status);
}
