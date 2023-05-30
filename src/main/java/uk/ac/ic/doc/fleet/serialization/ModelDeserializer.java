package uk.ac.ic.doc.fleet.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import uk.ac.ic.doc.fleet.entity.Model;

public class ModelDeserializer extends ObjectMapperDeserializer<Model> {
    public ModelDeserializer() {
        super(Model.class);
    }
}