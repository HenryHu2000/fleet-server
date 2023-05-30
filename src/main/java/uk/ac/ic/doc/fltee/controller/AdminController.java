package uk.ac.ic.doc.fltee.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import uk.ac.ic.doc.fltee.entity.Project;
import uk.ac.ic.doc.fltee.entity.Task;
import uk.ac.ic.doc.fltee.service.ITaskService;
import java.io.IOException;

@Path("/api/admin")
public class AdminController {
    @Inject
    private ITaskService taskService;
    @Channel("client-todo-tasks")
    private Emitter<Task> taskEmitter;

    @POST
    @Path("/create-project")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Project createProject(@FormParam("max_rounds") Integer maxRounds,
                                 @FormParam("buffer_size") Integer bufferSize) throws IOException, CloneNotSupportedException {
        if (maxRounds != null && bufferSize != null) {
            var taskOptional = taskService.createClientTask(maxRounds, bufferSize);
            if (taskOptional.isPresent()) {
                var newTask = taskOptional.get();
                taskEmitter.send(newTask);
                var projectDto = (Project) newTask.getProject().clone();
                projectDto.setCurrentModel(null);
                projectDto.setTasks(null);
                return projectDto;
            }
        }
        return null;
    }
}
