package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public abstract class GenericEventMixin {

    GenericEventMixin(
            @JsonProperty("_id") UUID identifier,
            @JsonProperty("location") LatLon location,
            @JsonProperty("active") boolean active,
            @JsonProperty("publication_time") LocalDateTime publicationTime,
            @JsonProperty("last_edit_time") LocalDateTime lastEditTime,
            @JsonProperty("description") String description,
            @JsonProperty("jams") Set<Jam> jams,
            @JsonProperty("event_type") EventType eventType,
            @JsonProperty("source") EventPublisher source,
            @JsonProperty("relevant_for_transportation_types") Set<TransportationType> relevantForTransportationTypes
    ) {
    }

    @JsonProperty("_id")
    public abstract UUID getIdentifier();

    @JsonProperty("location")
    public abstract LatLon getLocation();

    @JsonProperty("publication_time")
    public abstract LocalDateTime getPublicationTime();

    @JsonProperty("last_edit_time")
    public abstract LocalDateTime getLastEditTime();

    @JsonProperty("description")
    public abstract String getDescription();

    @JsonProperty("jams")
    public abstract Set<Jam> getJams();

    @JsonProperty("event_type")
    public abstract EventType getEventType();

    @JsonProperty("source")
    public abstract EventPublisher getSource();

    @JsonProperty("relevant_for_transportation_types")
    public abstract Set<TransportationType> getRelevantForTransportationTypes();

    @JsonProperty("active")
    private boolean active;

}
