package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.*;

/**
 * Business entity representing an event
 */
public abstract class Event extends Identifiable {
    private static final String LAST_EDIT_TIME_CAN_NOT_BE_NULL = "LastEditTime can not be null";
    private static final String CAN_NOT_CHANGE_INACTIVE_EVENT = "Can't change an inactive event";

    private final LatLon location;
    private final LocalDateTime publicationTime;
    private final EventPublisher source;
    private final EventType eventType;
    private LocalDateTime lastEditTime;
    private String description;
    private boolean active;
    private Set<Jam> jams;
    private Set<TransportationType> relevantForTransportationTypes;

    protected Event(UUID identifier, LatLon location, boolean active, LocalDateTime publicationTime, LocalDateTime lastEditTime, String description, Set<Jam> jams, EventType eventType, EventPublisher source, Set<TransportationType> relevantForTransportationTypes) throws ValidationException {
        super(identifier);
        notNull(location, "Location can not be null");
        this.location = location;
        this.active = active;
        notNull(publicationTime, "PublicationTime can not be null");
        this.publicationTime = publicationTime;
        notNull(lastEditTime, LAST_EDIT_TIME_CAN_NOT_BE_NULL);
        validationAssert(lastEditTime.isAfter(publicationTime) || lastEditTime.isEqual(publicationTime), "LastEditTime needs to be after PublicationTime");
        this.lastEditTime = lastEditTime;
        notNull(description, "Description can not be null");
        this.description = description;
        notNull(jams, "Jams can not be null");
        this.jams = new HashSet<>(jams);
        notNull(eventType, "EventType can not be null");
        this.eventType = eventType;
        notNull(source, "Source can not be null");
        this.source = source;
        notEmpty(relevantForTransportationTypes, "RelevantForTransportationTypes can not be empty");
        this.relevantForTransportationTypes = relevantForTransportationTypes;
    }

    protected Event(LatLon location, String description, Set<Jam> jams, EventType eventType, EventPublisher source, Set<TransportationType> relevantForTransportationTypes) throws ValidationException {
        this(UUID.randomUUID(), location, true, LocalDateTime.now(), LocalDateTime.now(), description, jams, eventType, source, relevantForTransportationTypes);
    }

    /**
     * @return The latlon of the location of this event. The coordinates of the place where the event is taking place.
     */
    public LatLon getLocation() {
        return location;
    }

    /**
     * @return The time of publication of this event.
     */
    public LocalDateTime getPublicationTime() {
        return publicationTime;
    }

    /**
     * @return
     */
    public LocalDateTime getLastEditTime() {
        return lastEditTime;
    }

    /**
     * @param lastEditTime The last time this event was updated.
     * @throws ValidationException
     */
    public void setLastEditTime(LocalDateTime lastEditTime) throws ValidationException {
        validationAssert(active, CAN_NOT_CHANGE_INACTIVE_EVENT);
        notNull(lastEditTime, LAST_EDIT_TIME_CAN_NOT_BE_NULL);
        validationAssert(lastEditTime.isAfter(this.lastEditTime) || lastEditTime.isEqual(this.lastEditTime), "New LastEditTime needs to be after previous LastEditTime");
        this.lastEditTime = lastEditTime;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description A description of the event.
     * @throws ValidationException
     */
    public void setDescription(String description) throws ValidationException {
        validationAssert(active, CAN_NOT_CHANGE_INACTIVE_EVENT);
        notNull(description, "Description can not be bull");
        this.description = description;
        lastEditTime = LocalDateTime.now();
    }

    /**
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active If this event is still taking place then active must be true, else it must be false.
     * @throws ValidationException
     */
    public void setActive(boolean active) throws ValidationException {
        this.active = active;
        lastEditTime = LocalDateTime.now();
    }

    /**
     * @return All the jams that are are connected to this event. An event may have no jams at all.
     */
    public Set<Jam> getJams() {
        return Collections.unmodifiableSet(jams);
    }

    /**
     * Add a new jam to this event
     * @param jam
     * @throws ValidationException
     */
    public void addToJams(Jam jam) throws ValidationException {
        validationAssert(active, CAN_NOT_CHANGE_INACTIVE_EVENT);
        notNull(jam, "Jam can not be null");
        jams.add(jam);
        lastEditTime = LocalDateTime.now();
    }

    /**
     * Remove a jam from this event
     * @param jam
     * @throws ValidationException
     */
    public void removeFromJams(Jam jam) throws ValidationException {
        validationAssert(active, CAN_NOT_CHANGE_INACTIVE_EVENT);
        jams.remove(jam);
        lastEditTime = LocalDateTime.now();
    }

    /**
     * @return The source from where this event was obtained. ex. WAZE.
     */
    public EventPublisher getSource() {
        return source;
    }

    /**
     * @return The EventType of this event. ex. CONSTRUCTION.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @return All transportationTypes for which this event is relevant.
     */
    public Set<TransportationType> getRelevantForTransportationTypes() {
        return Collections.unmodifiableSet(relevantForTransportationTypes);
    }

    /**
     * Add a new transportation type for which this event is relevant
     * @param transportationType
     * @throws ValidationException
     */
    public void addToRelevantForTransportationTypes(TransportationType transportationType) throws ValidationException {
        validationAssert(active, CAN_NOT_CHANGE_INACTIVE_EVENT);
        relevantForTransportationTypes.add(transportationType);
        lastEditTime = LocalDateTime.now();
    }

    /**
     * remove a transportation type for which this event isn't relevant anymore
     * @param transportationType
     * @throws ValidationException
     */
    public void removeFromRelevantForTransportationTypes(TransportationType transportationType) throws ValidationException {
        validationAssert(active, CAN_NOT_CHANGE_INACTIVE_EVENT);
        validationAssert(relevantForTransportationTypes.contains(transportationType), "This event does not contain " + transportationType);
        validationAssert(relevantForTransportationTypes.size() > 1, "Can't remove last TransportationType");
        relevantForTransportationTypes.remove(transportationType);
        lastEditTime = LocalDateTime.now();
    }

    /**
     * Makes a deepcopy of properties from another Event. The properties must be modifiable.
     * @param other: the event from which to make a deepcopy of the underlining properties.
     * @throws ValidationException
     */
    public void transferProperties(Event other) throws ValidationException {
        setDescription(other.getDescription());
        setLastEditTime(LocalDateTime.now());
        this.jams = new HashSet<>();
        for(Jam jam: other.getJams()) {
            this.jams.add(jam.deepCopy());
        }
        this.relevantForTransportationTypes = new HashSet<>(other.getRelevantForTransportationTypes());
        setActive(other.isActive());
    }
}
