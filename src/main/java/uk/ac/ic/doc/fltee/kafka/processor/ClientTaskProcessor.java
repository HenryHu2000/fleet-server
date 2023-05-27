package uk.ac.ic.doc.fltee.kafka.processor;

import io.quarkus.arc.Lock;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.jboss.logging.Logger;
import uk.ac.ic.doc.fltee.config.FlteeProperties;
import uk.ac.ic.doc.fltee.dao.ModelDao;
import uk.ac.ic.doc.fltee.dao.ProjectDao;
import uk.ac.ic.doc.fltee.dao.TaskDao;
import uk.ac.ic.doc.fltee.entity.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

@Lock
@ApplicationScoped
public class ClientTaskProcessor {
    private static final Logger LOG = Logger.getLogger(ClientTaskProcessor.class);
    @Inject
    private ModelDao modelDao;
    @Inject
    private ProjectDao projectDao;
    @Inject
    private TaskDao taskDao;
    @Inject
    private Map<Project, Queue<Model>> modelBufferMap;
    @Inject
    private FlteeProperties flteeProperties;
    @Channel("todo-tasks")
    private Emitter<Task> taskEmitter;

    @Lock(value = Lock.Type.WRITE)
    @Incoming("done-tasks")
    @Blocking
    @Transactional
    public Task process(Task completedTask) throws InterruptedException, IOException {
        var supertaskOptional = taskDao.findById(completedTask.getSupertask().getId());
        if (supertaskOptional.isPresent()) {
            var supertask = supertaskOptional.get();
            var project = supertask.getProject();
            if (!modelBufferMap.containsKey(project)) {
                modelBufferMap.put(project, new ArrayDeque<>());
            }
            var modelBuffer = modelBufferMap.get(project);
            completedTask.getOutputModels().forEach(outputModel -> {
                modelDao.save(outputModel);
                modelBuffer.offer(outputModel);
            });
            supertask.setTaskStatus(TaskStatus.RUNNING);
            supertask.getOutputModels().addAll(completedTask.getOutputModels());
            taskDao.save(supertask);
            completedTask.setProject(project);
            completedTask.setSupertask(supertask);
            completedTask.getInputModels().addAll(supertask.getInputModels());
            taskDao.save(completedTask);

            if (modelBuffer.size() >= flteeProperties.bufferSize()) {
                var i = 0;
                while (!modelBuffer.isEmpty()) {
                    var model = modelBuffer.poll();
                    var modelPath = flteeProperties.aggregatorPath() + "/results/mnist/client_updates_standard_ss/mnist_lenet_c" + i + ".weights/";
                    Files.createDirectories(Paths.get(modelPath));
                    Files.write(Paths.get(modelPath + "/_ree"), model.getRee());
                    Files.write(Paths.get(modelPath + "/_tee"), model.getTee());
                    i++;
                }
                ProcessBuilder pb = new ProcessBuilder(
                      "host/secure_aggregation_host server model_aggregation -pp_start 6 -pp_end 8 -ss 1 cfg/mnist_lenet.cfg ./results/mnist/client_updates_standard_ss/".split(" "));
                pb.directory(new File(flteeProperties.aggregatorPath()));
                var process = pb.start();
                LOG.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
                process.waitFor();
                var modelPath = flteeProperties.aggregatorPath() + "/results/mnist/client_updates_standard_ss/";
                var globalModel = new Model();
                var avgReeFile = Paths.get(modelPath + "/mnist_lenet_averaged.weights_ree");
                var avgTeeFile = Paths.get(modelPath + "/mnist_lenet_averaged.weights_tee");
                globalModel.setRee(Files.readAllBytes(avgReeFile));
                globalModel.setTee(Files.readAllBytes(avgTeeFile));
                Files.delete(avgReeFile);
                Files.delete(avgTeeFile);

                modelDao.save(globalModel);
                var newTask = new Task();
                newTask.setProject(project);
                newTask.setTaskType(TaskType.TRAINING);
                newTask.getInputModels().add(globalModel);
                taskDao.save(newTask);
                newTask.getProject().setTasks(null);
                taskEmitter.send(newTask);

            }
        }
        return completedTask;
    }
}
