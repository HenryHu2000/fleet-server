package uk.ac.ic.doc.fltee.processor;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import uk.ac.ic.doc.fltee.entity.*;
import uk.ac.ic.doc.fltee.service.ITaskService;

import java.io.IOException;

@ApplicationScoped
public class TaskProcessor {
    @Inject
    private ITaskService taskService;
    @Channel("outgoing-server-todo-tasks")
    private Emitter<Task> serverTaskEmitter;
    @Channel("client-todo-tasks")
    private Emitter<Task> clientTaskEmitter;

    @Incoming("client-done-tasks")
    @Blocking
    public Task processClientTask(Task task) {
        taskService.processClientTask(task).ifPresent(serverTaskEmitter::send);
        return task;
    }

    @Incoming("incoming-server-todo-tasks")
    @Blocking
    public Task processServerTask(Task task) throws InterruptedException, IOException {
        taskService.processServerTask(task).ifPresent(clientTaskEmitter::send);
        return task;
    }
}
