package uk.ac.ic.doc.fleet.service;

import uk.ac.ic.doc.fleet.entity.Project;

import java.util.Optional;

public interface IProjectService {

    Optional<Project> getProjectOverview(Long id) throws CloneNotSupportedException;

}
