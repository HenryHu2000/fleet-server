package uk.ac.ic.doc.fltee.assembly;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import uk.ac.ic.doc.fltee.entity.Model;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class FlteeProvider {
    private final AtomicInteger globalModelCounter = new AtomicInteger();
    private final Queue<Model> modelBuffer = new ArrayDeque<Model>();
    private final ReadWriteLock modelBufferLock = new ReentrantReadWriteLock();

    @Produces
    public AtomicInteger globalModelCounter(InjectionPoint injectionPoint) {
        return globalModelCounter;
    }
    @Produces
    public Queue<Model> modelBuffer(InjectionPoint injectionPoint) {
        return modelBuffer;
    }
    @Produces
    public ReadWriteLock modelBufferLock(InjectionPoint injectionPoint) {
        return modelBufferLock;
    }
}
