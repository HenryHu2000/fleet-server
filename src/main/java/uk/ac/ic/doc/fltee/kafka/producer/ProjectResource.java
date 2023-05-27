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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/projects")
public class ProjectResource {
    private static final Logger LOG = Logger.getLogger(ProjectResource.class);
    @Inject
    private FlteeProperties flteeProperties;
    @Inject
    private ModelDao modelDao;
    @Inject
    private ProjectDao projectDao;
    @Inject
    private TaskDao taskDao;
    @Channel("todo-tasks")
    private Emitter<Task> taskEmitter;

    @POST
    @Path("/create")
    @Produces(MediaType.TEXT_PLAIN)
    public String createRequest() throws IOException {
        var modelPath = flteeProperties.aggregatorPath() + "/results/mnist/";
        var globalModel = new Model();
        globalModel.setRee(Files.readAllBytes(Paths.get(modelPath + "/mnist_lenet_pp68.weights_ree")));
        globalModel.setTee(Files.readAllBytes(Paths.get(modelPath + "/mnist_lenet_pp68.weights_tee")));

        var project = new Project();
        projectDao.save(project);

        modelDao.save(globalModel);
        var newTask = new Task();
        newTask.setProject(project);
        newTask.setTaskType(TaskType.TRAINING);
        newTask.getInputModels().add(globalModel);
        taskDao.save(newTask);
        newTask.getProject().setTasks(null);
        taskEmitter.send(newTask);

        return project.getId().toString();
    }
}
