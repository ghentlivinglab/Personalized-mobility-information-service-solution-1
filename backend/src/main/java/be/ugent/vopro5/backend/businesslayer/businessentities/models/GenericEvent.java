package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * business entity which represents a generic event
 */
public class GenericEvent extends Event {

    /**
     * create a new generic event. This can be any kind of incident related to traffic. ex. Road obstructions, slippery
     * roads ...
     * @param identifier The UUID of the event.
     * @param location The coordinates of the location where this event is happening.
     * @param active Whether this event is still taking place or not.
     * @param publicationTime The time of publication of this event.
     * @param lastEditTime The last time this event was updated.
     * @param description A description of the event. Usually this is some extra information about the event.
     * @param jams All the jams that are connected to this event. An event may have no jams at all.
     * @param eventType The EventType of this event. ex. CONSTRUCTION.
     * @param source The source from where this event was obtained. ex. WAZE.
     * @param relevantForTransportationTypes All transportationTypes for which this event is relevant.
     * @throws ValidationException
     */
    public GenericEvent(UUID identifier, LatLon location, boolean active, LocalDateTime publicationTime, LocalDateTime lastEditTime, String description, Set<Jam> jams, EventType eventType, EventPublisher source, Set<TransportationType> relevantForTransportationTypes) throws ValidationException {
        super(identifier, location, active, publicationTime, lastEditTime, description, jams, eventType, source, relevantForTransportationTypes);
    }

    /**
     * create a new generic event
     * @param location
     * @param description
     * @param jams
     * @param eventType
     * @param source
     * @param relevantForTransportationTypes
     * @throws ValidationException
     */
    public GenericEvent(LatLon location, String description, Set<Jam> jams, EventType eventType, EventPublisher source, Set<TransportationType> relevantForTransportationTypes) throws ValidationException {
        super(location, description, jams, eventType, source, relevantForTransportationTypes);
    }
}
