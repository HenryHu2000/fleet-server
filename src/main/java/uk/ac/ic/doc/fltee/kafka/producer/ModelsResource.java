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
import uk.ac.ic.doc.fltee.entity.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/models")
public class ModelsResource {
    private static final Logger LOG = Logger.getLogger(ModelsResource.class);

    @Inject
    private FlteeProperties flteeProperties;

    @Inject
    private AtomicInteger globalModelCounter;

    @Channel("global-models")
    private Emitter<Model> modelEmitter;

    @POST
    @Path("/create")
    @Produces(MediaType.TEXT_PLAIN)
    public String createRequest() throws IOException {
        var modelPath = flteeProperties.aggregatorPath() + "/results/mnist/";
        var globalModel = new Model();
        globalModel.setName(String.valueOf(globalModelCounter.getAndIncrement()));
        globalModel.setRee(Files.readAllBytes(Paths.get(modelPath + "/mnist_lenet_pp68.weights_ree")));
        globalModel.setTee(Files.readAllBytes(Paths.get(modelPath + "/mnist_lenet_pp68.weights_tee")));
        modelEmitter.send(globalModel);
        return "Successful";
    }
}
