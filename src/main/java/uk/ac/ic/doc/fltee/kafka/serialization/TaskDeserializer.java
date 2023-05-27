package uk.ac.ic.doc.fltee.kafka.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import uk.ac.ic.doc.fltee.entity.Task;

public class TaskDeserializer extends ObjectMapperDeserializer<Task> {
    public TaskDeserializer() {
        super(Task.class);
    }
}