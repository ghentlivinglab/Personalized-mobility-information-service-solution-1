package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.GenericEvent;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * Created by evert on 23/03/16.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "class")
@JsonSubTypes({@JsonSubTypes.Type(GenericEvent.class), @JsonSubTypes.Type(WazeEvent.class)})
@JsonTypeIdResolver(EventTypeResolver.class)
public abstract class EventMixin {
}
