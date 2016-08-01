package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEventPublisher;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "class")
@JsonSubTypes({@JsonSubTypes.Type(WazeEventPublisher.class)})
@JsonTypeIdResolver(EventPublisherTypeResolver.class)
public abstract class EventPublisherMixin {
}
