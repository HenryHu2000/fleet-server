package uk.ac.ic.doc.fleet.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import uk.ac.ic.doc.fleet.entity.Task;

public class TaskDeserializer extends ObjectMapperDeserializer<Task> {
    public TaskDeserializer() {
        super(Task.class);
    }
}