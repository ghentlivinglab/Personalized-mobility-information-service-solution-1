package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.*;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.*;

/**
 * Created on 24/02/16.
 */
public class Route extends Identifiable {
    private final List<LatLon> waypoints;
    private final TransportationType transportationType;
    private Set<EventType> notifyForEventTypes;
    private Set<NotificationMedium.NotificationMediumType> notificationMedia;
    private boolean active;

    /**
     * create a new route. A route can be seen as a concrete way to reach a destination departing from
     * a starting point using a certain type of transportation. A route cannot exist without a travel because a travel
     * is an abstraction that contains the start and endpoint. A travel can therefore have multiple unique routes that each
     * define a different way to reach the endpoint departing from the starting point.
     * @param identifier The UUID of the route.
     * @param waypoints The coordinates that determine the way to reach the endpoint who is defined in the travel the route belongs to.
     * @param transportationType The type of transportation that the user uses for this route. ex. CAR
     * @param notifyForEventTypes The types of events the user wants to receive notifications from for this particular route.
     * @param notificationMedia Notifications relevant to the route will only be sent to the a user's
     *                          notification medium if the type of that medium is in the set notificationMedia. ex. If a
     *                          user wants notifications of the route to his work address sent to his/her email
     *                          address then the set notificationMedia should contain the type EMAIL.
     * @param active True if the user wants to receive notifications for this route, else this should be false.
     * @throws ValidationException
     */
    public Route(UUID identifier, List<LatLon> waypoints, TransportationType transportationType,
                 Set<EventType> notifyForEventTypes, Set<NotificationMedium.NotificationMediumType> notificationMedia, boolean active) throws ValidationException {
        super(identifier);
        notEmpty(waypoints, "Waypoints can not be empty");
        this.waypoints = new ArrayList<>(waypoints);
        notNull(transportationType, "Transportation type can not be null");
        this.transportationType = transportationType;
        notEmpty(notifyForEventTypes, "NotifyForEventTypes can not be empty");
        this.notifyForEventTypes = new HashSet<>(notifyForEventTypes);
        notNull(notificationMedia, "NotificationMedia can not be empty");
        this.notificationMedia = new HashSet<>(notificationMedia);
        this.active = active;
    }

    /**
     * create a new route
     * @param waypoints
     * @param transportationType
     * @param notifyForEventTypes
     * @param notificationMedia
     * @param active
     * @throws ValidationException
     */
    public Route(List<LatLon> waypoints, TransportationType transportationType,
                 Set<EventType> notifyForEventTypes, Set<NotificationMedium.NotificationMediumType> notificationMedia, boolean active) throws ValidationException {
        this(UUID.randomUUID(), waypoints, transportationType, notifyForEventTypes, notificationMedia, active);
    }

    /**
     * @return A list of coordinates which represent locations that are part of this route.
     */
    public List<LatLon> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }

    /**
     * @return
     */
    public TransportationType getTransportationType() {
        return transportationType;
    }

    /**
     * @return
     */
    public Set<EventType> getNotifyForEventTypes() {
        return Collections.unmodifiableSet(notifyForEventTypes);
    }

    /**
     * add a new event type for which the user should be nortified
     * @param eventType
     * @throws ValidationException
     */
    public void addToNotifyForEventTypes(EventType eventType) throws ValidationException {
        notNull(eventType, "Can't add null to NotifyForEventTypes");
        notifyForEventTypes.add(eventType);
    }

    /**
     * remove an event type
     * @param eventType
     * @throws ValidationException
     */
    public void removeFromNotifyForEventTypes(EventType eventType) throws ValidationException {
        validationAssert(notifyForEventTypes.contains(eventType), "This point of interest does not contain " + eventType.toString());
        validationAssert(notifyForEventTypes.size() > 1, "Cannot remove last element from notifyForEventTypes");
        notifyForEventTypes.remove(eventType);
    }

    /**
     * @return
     */
    public Set<NotificationMedium.NotificationMediumType> getNotificationMedia() {
        return Collections.unmodifiableSet(notificationMedia);
    }

    /**
     * add a notification medium to this route
     * @param notificationMedium
     * @throws ValidationException
     */
    public void addToNotificationMedia(NotificationMedium.NotificationMediumType notificationMedium) throws ValidationException {
        notNull(notificationMedium, "Can't add null to NotificationMedia");
        notificationMedia.add(notificationMedium);
    }

    /**
     * remove a notification medium from this route
     * @param notificationMedium
     * @throws ValidationException
     */
    public void removeFromNotificationMedia(NotificationMedium.NotificationMediumType notificationMedium) throws ValidationException {
        validationAssert(notificationMedia.contains(notificationMedium), "This route does not contain " + notificationMedium.toString());
        validationAssert(notificationMedia.size() > 1, "Can't remove last element from NotificationMedia");
        notificationMedia.remove(notificationMedium);
    }

    /**
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Makes a deepcopy of properties from another Route. The properties must be modifiable.
     * @param other: the route from which to make a deepcopy of the underlining properties.
     */
    public void transferProperties(Route other) {
        this.notifyForEventTypes = new HashSet<>(other.notifyForEventTypes);
        this.notificationMedia = new HashSet<>(other.notificationMedia);
        this.active = other.isActive();
    }
}
