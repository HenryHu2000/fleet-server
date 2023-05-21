package uk.ac.ic.doc.fltee.kafka.processor;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.jboss.logging.Logger;
import uk.ac.ic.doc.fltee.dao.ModelDao;
import uk.ac.ic.doc.fltee.entity.Model;

@ApplicationScoped
public class ModelsProcessor {
    private static final Logger LOG = Logger.getLogger(ModelsProcessor.class);
    @Inject
    ModelDao modelDao;

    @Incoming("aggregation-models")
    @Blocking
    public Model process(Model modelRequest) throws InterruptedException {
        modelDao.save(modelRequest);
        return modelRequest;
    }
}
