package uk.ac.ic.doc.fleet.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import uk.ac.ic.doc.fleet.dao.ProjectDao;
import uk.ac.ic.doc.fleet.entity.*;
import uk.ac.ic.doc.fleet.service.IProjectService;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectService implements IProjectService {
    @Inject
    private ProjectDao projectDao;

    public Optional<Project> getProjectOverview(Long id) throws CloneNotSupportedException {
        var projectOptional = projectDao.findById(id);
        if (projectOptional.isPresent()) {
            var project = projectOptional.get();
            var projectDto = (Project) project.clone();
            var tasks = new ArrayList<Task>();
            for (var task : projectDto.getTasks()) {
                var taskDto = (Task) task.clone();
                taskDto.setProject(null);
                if (taskDto.getUser() != null) {
                    var userDto = new User();
                    userDto.setId(taskDto.getUser().getId());
                    userDto.setSecurityLevel(null);
                    userDto.setScore(null);
                    taskDto.setUser(userDto);
                }
                if (taskDto.getDevice() != null) {
                    var deviceDto = new Device();
                    deviceDto.setId(taskDto.getDevice().getId());
                    taskDto.setDevice(deviceDto);
                }
                taskDto.setSupertask(buildIdOnlyTask(taskDto.getSupertask()));
                taskDto.setSubtasks(taskDto.getSubtasks().stream().map(this::buildIdOnlyTask).collect(Collectors.toList()));
                taskDto.setInputModels(taskDto.getInputModels().stream().map(this::buildIdOnlyModel).collect(Collectors.toList()));
                taskDto.setOutputModels(taskDto.getOutputModels().stream().map(this::buildIdOnlyModel).collect(Collectors.toList()));
                tasks.add(taskDto);
            }
            projectDto.setTasks(tasks);
            projectDto.setCurrentModel(buildIdOnlyModel(projectDto.getCurrentModel()));
            return Optional.of(projectDto);
        }

        return Optional.empty();
    }

    private Task buildIdOnlyTask(Task task) {
        if (task == null) {
            return null;
        }
        var taskDto = new Task();
        taskDto.setId(task.getId());
        taskDto.setDateCreated(task.getDateCreated());
        taskDto.setDateModified(task.getDateModified());
        taskDto.setStatus(null);
        taskDto.setSubtasks(null);
        taskDto.setInputModels(null);
        taskDto.setOutputModels(null);
        return taskDto;
    }

    private Model buildIdOnlyModel(Model model) {
        if (model == null) {
            return null;
        }
        var modelDto = new Model();
        modelDto.setId(model.getId());
        modelDto.setDateCreated(model.getDateCreated());
        modelDto.setDateModified(model.getDateModified());
        modelDto.setConsumerTasks(null);
        modelDto.setProducerTasks(null);
        return modelDto;
    }

    public Optional<Status> setProjectStatus(Long id, Status status) {
        var projectOptional = projectDao.findById(id);
        if (projectOptional.isPresent()) {
            var project = projectOptional.get();
            project.setStatus(status);
            projectDao.save(project);
            return Optional.of(project.getStatus());
        }
        return Optional.empty();
    }
}