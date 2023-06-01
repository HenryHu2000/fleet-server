package uk.ac.ic.doc.fleet.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.quarkus.arc.Lock;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import uk.ac.ic.doc.fleet.config.FleetProperties;
import uk.ac.ic.doc.fleet.dao.*;
import uk.ac.ic.doc.fleet.entity.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import uk.ac.ic.doc.fleet.service.ITaskService;

@Lock
@ApplicationScoped
public class TaskService implements ITaskService {
    private static final Logger LOG = Logger.getLogger(TaskService.class);
    @Inject
    private ModelDao modelDao;
    @Inject
    private ProjectDao projectDao;
    @Inject
    private TaskDao taskDao;
    @Inject
    private UserDao userDao;
    @Inject
    private DeviceDao deviceDao;
    @Inject
    private Map<Project, Queue<Model>> modelBufferMap;
    @Inject
    private FleetProperties fleetProperties;
    @Inject
    MeterRegistry registry;

    @Transactional
    public Optional<Task> processClientTask(Task clientSubtask) {
        if (clientSubtask.getSupertask() == null) {
            return Optional.empty();
        }
        var supertaskOptional = taskDao.findById(clientSubtask.getSupertask().getId());
        if (supertaskOptional.isPresent() && supertaskOptional.get().getProject().getStatus().equals(Status.RUNNING)) {
            var clientSupertask = supertaskOptional.get();
            var project = clientSupertask.getProject();
            if (project.getRound() >= project.getMaxRounds()) {
                project.setStatus(Status.COMPLETED);
                projectDao.save(project);
                for (var task: project.getTasks()) {
                    if (task.getStatus().equals(Status.RUNNING)) {
                        task.setStatus(Status.COMPLETED);
                        taskDao.save(task);
                    }
                }
                return Optional.empty();
            }

            var userOptional = userDao.findById(clientSubtask.getUser().getId());
            if (!userOptional.isPresent()) {
                return Optional.empty();
            }
            var user = userOptional.get();
            clientSubtask.setUser(user);
            var deviceOptional = deviceDao.findById(clientSubtask.getDevice().getId());
            var device = clientSubtask.getDevice();
            if (deviceOptional.isPresent()) {
                device.setVersion(deviceOptional.get().getVersion());
            }
            deviceDao.save(device);
            if (user.getSecurityLevel() < project.getMinUserLevel() || device.getSecurityLevel() < project.getMinDeviceLevel()) {
                return Optional.empty();
            }

            if (!modelBufferMap.containsKey(project)) {
                modelBufferMap.put(project, new ArrayDeque<>());
            }
            var modelBuffer = modelBufferMap.get(project);
            clientSubtask.getOutputModels().forEach(outputModel -> {
                modelDao.save(outputModel);
                modelBuffer.offer(outputModel);
            });
            clientSupertask.getOutputModels().addAll(clientSubtask.getOutputModels());
            taskDao.save(clientSupertask);
            clientSubtask.setProject(project);
            clientSubtask.setSupertask(clientSupertask);
            clientSubtask.setRound(clientSupertask.getRound());
            clientSubtask.getInputModels().addAll(clientSupertask.getInputModels());
            taskDao.save(clientSubtask);

            if (modelBuffer.size() >= project.getBufferSize()) {
                var serverTask = new Task();
                serverTask.setProject(project);
                serverTask.setRound(project.getRound());
                serverTask.setTaskType(TaskType.AGGREGATION);
                while (!modelBuffer.isEmpty()) {
                    var model = modelBuffer.poll();
                    serverTask.getInputModels().add(model);
                }
                taskDao.save(serverTask);
                var projectDto = new Project();
                projectDto.setId(project.getId());
                serverTask.setProject(projectDto);
                return Optional.of(serverTask);
            }
        }
        return Optional.empty();
    }

    @Lock(value = Lock.Type.WRITE)
    @Transactional
    public Optional<Task> processServerTask(Task serverTask) throws IOException, InterruptedException {
        if (serverTask.getProject() == null) {
            return Optional.empty();
        }
        var projectOptional = projectDao.findById(serverTask.getProject().getId());
        if (projectOptional.isPresent() && projectOptional.get().getStatus().equals(Status.RUNNING)) {
            var project = projectOptional.get();
            String projectPath = '/' + project.getName() + '/';
            String fullProjectPath = fleetProperties.aggregatorPath() + "/results/mnist/" + projectPath;
            FileUtils.deleteDirectory(new File(fullProjectPath));
            var inputModels = serverTask.getInputModels();
            for (int i = 0; i < inputModels.size(); i++) {
                var model = inputModels.get(i);
                var modelPath = fullProjectPath + "/mnist_lenet_c" + i + ".weights/";
                Files.createDirectories(Paths.get(modelPath));
                Files.write(Paths.get(modelPath + "/_ree"), model.getRee());
                Files.write(Paths.get(modelPath + "/_tee"), model.getTee());
            }
            ProcessBuilder pb = new ProcessBuilder(
                    ("host/secure_aggregation_host server model_aggregation -pp_start 6 -pp_end 8 -ss 1 cfg/mnist_lenet.cfg ./results/mnist/"
                            + projectPath).split(" "));
            pb.directory(new File(fleetProperties.aggregatorPath()));
            var process = pb.start();
            // User score changes
            try (var scanner = new Scanner(process.getErrorStream())) {
                while (scanner.hasNextLine()) {
                    var line = scanner.nextLine();
                    final var key = "outlier_fractions";
                    if (line.startsWith(key)) {
                        var outlierFractions =
                                Arrays.stream(line.substring(key.length() + 2, line.length() - 1).split(", "))
                                        .mapToDouble(Double::parseDouble)
                                        .toArray();
                        LOG.info(key + '=' + Arrays.toString(outlierFractions));
                        for (int i = 0; i < outlierFractions.length; i++) {
                            double outlierFraction = outlierFractions[i];
                            modelDao.findById(inputModels.get(i).getId()).ifPresent(model ->
                                model.getProducerTasks().forEach(task -> {
                                    var user = task.getUser();
                                    if (user != null) {
                                        if (outlierFraction > 1E-5) {
                                            registry.counter("warning_counter", Tags.of("name", "outlier")).increment();
                                        }
                                        user.setScore(user.getScore() - outlierFraction);
                                        userDao.save(user);
                                    }
                                })
                            );
                        }
                    }
                }
            }
            LOG.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            process.waitFor();
            var modelPath = fleetProperties.aggregatorPath() + "/results/mnist/" + projectPath;
            var globalModel = new Model();
            var avgReeFile = Paths.get(modelPath + "/mnist_lenet_averaged.weights_ree");
            var avgTeeFile = Paths.get(modelPath + "/mnist_lenet_averaged.weights_tee");
            globalModel.setRee(Files.readAllBytes(avgReeFile));
            globalModel.setTee(Files.readAllBytes(avgTeeFile));
            Files.delete(avgReeFile);
            Files.delete(avgTeeFile);

            modelDao.save(globalModel);
            serverTask.setProject(project);
            serverTask.setStatus(Status.COMPLETED);
            serverTask.getOutputModels().add(globalModel);
            taskDao.save(serverTask);
            project.setRound(project.getRound() + 1);
            project.setCurrentModel(globalModel);
            projectDao.save(project);

            var newTask = buildTask(project, globalModel);
            taskDao.save(newTask);
            var projectDto = new Project();
            projectDto.setId(project.getId());
            newTask.setProject(projectDto);
            return Optional.of(newTask);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Task> createClientTask(int maxRounds, int bufferSize, int minUserLevel, int minDeviceLevel) throws IOException {
        var modelPath = fleetProperties.aggregatorPath() + "/results/mnist/";
        var globalModel = new Model();
        globalModel.setRee(Files.readAllBytes(Paths.get(modelPath + "/mnist_lenet_pp68.weights_ree")));
        globalModel.setTee(Files.readAllBytes(Paths.get(modelPath + "/mnist_lenet_pp68.weights_tee")));
        modelDao.save(globalModel);

        var project = new Project();
        project.setMaxRounds(maxRounds);
        project.setBufferSize(bufferSize);
        project.setMinUserLevel(minUserLevel);
        project.setMinDeviceLevel(minDeviceLevel);
        project.setCurrentModel(globalModel);
        project.setStatus(Status.RUNNING);
        projectDao.save(project);
        project.setName("proj" + project.getId());
        projectDao.save(project);

        var newTask = buildTask(project, globalModel);
        taskDao.save(newTask);

        return Optional.of(newTask);
    }

    private Task buildTask(Project project, Model model) {
        var task = new Task();
        task.setProject(project);
        task.setRound(project.getRound());
        task.setTaskType(TaskType.TRAINING);
        task.setStatus(Status.RUNNING);
        task.getInputModels().add(model);
        return task;
    }

}
