package uk.ac.ic.doc.fltee.kafka.processor;

import io.quarkus.arc.Lock;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.jboss.logging.Logger;
import uk.ac.ic.doc.fltee.config.FlteeProperties;
import uk.ac.ic.doc.fltee.dao.ModelDao;
import uk.ac.ic.doc.fltee.entity.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;

@Lock
@ApplicationScoped
public class ModelsProcessor {
    private static final Logger LOG = Logger.getLogger(ModelsProcessor.class);
    @Inject
    private ModelDao modelDao;
    @Inject
    private Queue<Model> modelBuffer;
    @Inject
    private FlteeProperties flteeProperties;
    @Inject
    private AtomicInteger globalModelCounter;
    @Inject
    private ReadWriteLock modelBufferLock;
    @Channel("global-models")
    private Emitter<Model> modelEmitter;

    @Lock(value = Lock.Type.WRITE)
    @Incoming("local-models")
    @Blocking
    public Model process(Model localModel) throws InterruptedException, IOException {
          modelBuffer.offer(localModel);
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
              // pb.inheritIO();
              pb.directory(new File(flteeProperties.aggregatorPath()));
              var process = pb.start();
              LOG.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
              process.waitFor();
              var modelPath = flteeProperties.aggregatorPath() + "/results/mnist/client_updates_standard_ss/";
              var model = new Model();
              model.setName(String.valueOf(globalModelCounter.getAndIncrement()));
              var avgReeFile = Paths.get(modelPath + "/mnist_lenet_averaged.weights_ree");
              var avgTeeFile = Paths.get(modelPath + "/mnist_lenet_averaged.weights_tee");
              model.setRee(Files.readAllBytes(avgReeFile));
              model.setTee(Files.readAllBytes(avgTeeFile));
              Files.delete(avgReeFile);
              Files.delete(avgTeeFile);
              modelEmitter.send(model);
          }
          modelDao.save(localModel);
          return localModel;
    }
}
