package uk.ac.ic.doc.fleet.assembly;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import uk.ac.ic.doc.fleet.entity.Model;
import uk.ac.ic.doc.fleet.entity.Project;

import java.util.*;

@ApplicationScoped
public class AppProvider {
    private final Map<Project, Queue<Model>> modelBufferMap = Collections.synchronizedMap(new HashMap<>());

    @Produces
    public Map<Project, Queue<Model>> modelBufferMap(InjectionPoint injectionPoint) {
        return modelBufferMap;
    }
}
