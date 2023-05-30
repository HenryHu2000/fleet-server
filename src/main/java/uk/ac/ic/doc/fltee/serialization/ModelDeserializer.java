package uk.ac.ic.doc.fltee.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import uk.ac.ic.doc.fltee.entity.Model;

public class ModelDeserializer extends ObjectMapperDeserializer<Model> {
    public ModelDeserializer() {
        super(Model.class);
    }
}