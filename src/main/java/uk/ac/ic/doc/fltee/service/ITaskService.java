package uk.ac.ic.doc.fltee.service;

import uk.ac.ic.doc.fltee.entity.Task;

import java.io.IOException;
import java.util.Optional;

public interface ITaskService {
    Optional<Task> processClientTask(Task clientSubtask);

    Optional<Task> processServerTask(Task serverTask) throws IOException, InterruptedException;

    Optional<Task> createClientTask(int maxRounds, int bufferSize) throws IOException;
}
