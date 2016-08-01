package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by thibault on 3/21/16.
 */
public abstract class EventMixin {
    @JsonProperty("coordinates")
    private LatLon location;
    @JsonProperty(value = "publication_time", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime publicationTime;
    @JsonProperty("source")
    private EventPublisher source;
    @JsonProperty("type")
    @JsonSerialize(using = EventTypeSerializer.class)
    private EventType eventType;
    @JsonProperty(value = "last_edit_time", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastEditTime;
    @JsonProperty("description")
    private String description;
    @JsonProperty("active")
    private boolean active;
    @JsonProperty("jams")
    private Set<Jam> jams;
    @JsonProperty("relevant_for_transportation_types")
    private Set<TransportationType> relevantForTransportationTypes;

    @JsonCreator
    public EventMixin(
            @JsonProperty("coordinates") LatLon location,
            @JsonProperty("description") String description,
            @JsonProperty("jams") Set<Jam> jams,
            @JsonProperty("type") @JsonDeserialize(using = EventTypeDeserializer.class) EventType eventType,
            @JsonProperty("source") EventPublisher source,
            @JsonProperty("relevant_for_transportation_types") Set<TransportationType> relevantForTransportationTypes
    ) {
    }

    public static class EventTypeDeserializer extends JsonDeserializer<EventType> {
        @Override
        public EventType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectNode node = p.readValueAsTree();
            String type = node.get("type").asText();
            try {
                return EventType.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("EventType does not exist: " + type);
            }
        }
    }

    public static class EventTypeSerializer extends JsonSerializer<EventType> {
        @Override
        public void serialize(EventType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type",value.toString());
            gen.writeEndObject();
        }
    }
}
