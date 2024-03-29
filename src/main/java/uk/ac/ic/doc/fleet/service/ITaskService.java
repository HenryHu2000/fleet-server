package uk.ac.ic.doc.fleet.service;

import uk.ac.ic.doc.fleet.entity.Project;
import uk.ac.ic.doc.fleet.entity.Task;

import java.io.IOException;
import java.util.Optional;

public interface ITaskService {
    Optional<Task> processClientTask(Task clientSubtask);

    Optional<Task> processServerTask(Task serverTask) throws IOException, InterruptedException;

    Optional<Task> createClientTask(int maxRounds, int bufferSize, int minUserLevel, int minDeviceLevel) throws IOException;
}
