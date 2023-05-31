package uk.ac.ic.doc.fleet.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import uk.ac.ic.doc.fleet.entity.Project;
import uk.ac.ic.doc.fleet.entity.Task;
import uk.ac.ic.doc.fleet.service.ITaskService;
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
                                 @FormParam("buffer_size") Integer bufferSize,
                                 @FormParam("min_user_level") @DefaultValue("0") Integer minUserLevel,
                                 @FormParam("min_device_level") @DefaultValue("0") Integer minDeviceLevel
                                 ) throws IOException, CloneNotSupportedException {
        if (maxRounds != null && bufferSize != null) {
            var taskOptional
                    = taskService.createClientTask(maxRounds, bufferSize, minUserLevel, minDeviceLevel);
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
