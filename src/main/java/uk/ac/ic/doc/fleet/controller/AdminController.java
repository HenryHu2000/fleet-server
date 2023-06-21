package uk.ac.ic.doc.fleet.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import uk.ac.ic.doc.fleet.dao.ProjectDao;
import uk.ac.ic.doc.fleet.entity.Project;
import uk.ac.ic.doc.fleet.entity.Status;
import uk.ac.ic.doc.fleet.entity.Task;
import uk.ac.ic.doc.fleet.service.IProjectService;
import uk.ac.ic.doc.fleet.service.ITaskService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Path("/api/admin")
public class AdminController {
    @Inject
    private IProjectService projectService;
    @Inject
    private ITaskService taskService;
    @Channel("client-todo-tasks")
    private Emitter<Task> taskEmitter;

    @POST
    @Path("/create-project")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(@FormParam("max_rounds") Integer maxRounds,
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
                return Response.ok(projectDto).build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @GET
    @Path("/lookup-project/{id}")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupProject(@PathParam(value = "id") Long id) throws CloneNotSupportedException {
        var projectOptional = projectService.getProjectOverview(id);
        if (projectOptional.isPresent()) {
            var project = projectOptional.get();
            return Response.ok(project).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/pause-project/{id}")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pauseProject(@PathParam(value = "id") Long id) {
        var statusOptional = projectService.setProjectStatus(id, Status.PAUSED);
        if (statusOptional.isPresent()) {
            var status = statusOptional.get();
            return Response.ok(status).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/resume-project/{id}")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public Response resumeProject(@PathParam(value = "id") Long id) {
        var statusOptional = projectService.setProjectStatus(id, Status.RUNNING);
        if (statusOptional.isPresent()) {
            var status = statusOptional.get();
            return Response.ok(status).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
