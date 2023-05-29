package uk.ac.ic.doc.fltee.kafka.producer;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import uk.ac.ic.doc.fltee.config.FlteeProperties;
import uk.ac.ic.doc.fltee.dao.ModelDao;
import uk.ac.ic.doc.fltee.dao.ProjectDao;
import uk.ac.ic.doc.fltee.dao.TaskDao;
import uk.ac.ic.doc.fltee.entity.Model;
import uk.ac.ic.doc.fltee.entity.Project;
import uk.ac.ic.doc.fltee.entity.Task;
import uk.ac.ic.doc.fltee.entity.TaskType;
import uk.ac.ic.doc.fltee.service.ITaskService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/projects")
public class ProjectResource {
    @Inject
    private ITaskService taskService;
    @Channel("client-todo-tasks")
    private Emitter<Task> taskEmitter;

    @POST
    @Path("/create")
    @Produces(MediaType.TEXT_PLAIN)
    public String createRequest() throws IOException {
        var taskOptional = taskService.createClientTask();
        if (taskOptional.isPresent()) {
            var newTask = taskOptional.get();
            taskEmitter.send(newTask);
            return newTask.getProject().getId().toString();
        }
        return "";
    }
}
