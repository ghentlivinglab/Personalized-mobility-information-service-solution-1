package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.*;

/**
 * Created on 25/02/16.
 */
public class PointOfInterest extends Identifiable {
    private static final String NAME_CAN_NOT_BE_BLANK = "Name can not be blank";
    private static final String RADIUS_CAN_NOT_BE_NEGATIVE = "Radius can not be negative";
    private static final String THIS_POI_DOES_NOT_CONTAIN = "This point of interest does not contain ";

    private final Address address;
    private String name;
    private int radius;
    private Set<EventType> notifyForEventTypes;
    private Set<NotificationMedium.NotificationMediumType> notificationMedia;
    private boolean active;

    /**
     * create a new point of interest. A point of interest can be any location relevant to a user. Once a point of
     * interest is created the user, he/she may choose to receive notifications about events in a certain radius around
     * the chosen location.
     * @param identifier
     * @param address The address of the location.
     * @param name The name given to this particular point of interest by the user. If a user decides to create a point
     *             of interest for his home address he could name it "best place ever".
     * @param radius The radius around the address wherein the user wants to be notified of events. ex. A user may want to
     *               receive notifications of events in a radius of 1km of his home address
     * @param notifyForEventTypes
     * @param notificationMedia Notifications relevant to the point of interest will only be sent to the a user's
     *                          notification medium if the type of that medium is in the set notificationMedia. ex. If a
     *                          user wants notifications of the point of interest of his work address sent to his/her email
     *                          address then the set notificationMedia should contain the type EMAIL.
     * @param active True if the user wants to receive notifications for this point of interest, else this should be false.
     * @throws ValidationException
     */
    public PointOfInterest(UUID identifier, Address address, String name, int radius, Set<EventType> notifyForEventTypes, Set<NotificationMedium.NotificationMediumType> notificationMedia, boolean active) throws ValidationException {
        super(identifier);
        notNull(address, "Address can not be null");
        this.address = address;
        notBlank(name, NAME_CAN_NOT_BE_BLANK);
        this.name = name;
        positive(radius, RADIUS_CAN_NOT_BE_NEGATIVE);
        this.radius = radius;
        notEmpty(notifyForEventTypes, "NotifyForEventTypes can not be empty");
        this.notifyForEventTypes = notifyForEventTypes;
        notNull(notificationMedia, "NotificationMedia can not be null");
        this.notificationMedia = notificationMedia;
        this.active = active ;

        if(radius == 0){
            this.active = false;
        }

    }

    /**
     * create a new point of interest
     * @param address
     * @param name
     * @param radius
     * @param notifyForEventTypes
     * @param notificationMedia
     * @param active
     * @throws ValidationException
     */
    public PointOfInterest(Address address, String name, int radius, Set<EventType> notifyForEventTypes,
                           Set<NotificationMedium.NotificationMediumType> notificationMedia, boolean active) throws ValidationException {
        this(UUID.randomUUID(),address,name,radius,notifyForEventTypes,notificationMedia,active);
    }

    /**
     * @return The address of this point of interest.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @return The name given to this point of interest.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to be set.
     */
    public void setName(String name) throws ValidationException {
        notBlank(name, NAME_CAN_NOT_BE_BLANK);
        this.name = name;
    }

    /**
     * @return The radius around the address wherein the user wants to be notified of events. ex. A user may want to
     * receive notifications of events in a radius of 1km of his home address.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @param radius The radius to be set.
     */
    public void setRadius(int radius) throws ValidationException {
        positive(radius, RADIUS_CAN_NOT_BE_NEGATIVE);
        this.radius = radius;
    }

    /**
     * @return
     */
    public Set<EventType> getNotifyForEventTypes() {
        return Collections.unmodifiableSet(notifyForEventTypes);
    }

    /**
     * add a new event type for which the user should be notified
     * @param eventType
     * @throws ValidationException
     */
    public void addToNotifyForEventTypes(EventType eventType) throws ValidationException {
        notNull(eventType, "EventType can not be null");
        notifyForEventTypes.add(eventType);
    }

    /**
     * remove an event type
     * @param eventType
     * @throws ValidationException
     */
    public void removeFromNotifyFromEventTypes(EventType eventType) throws ValidationException {
        validationAssert(notifyForEventTypes.contains(eventType), THIS_POI_DOES_NOT_CONTAIN + eventType.toString());
        validationAssert(notifyForEventTypes.size() > 1, "Cannot remove last element from notifyForEventTypes");
        notifyForEventTypes.remove(eventType);
    }

    public Set<NotificationMedium.NotificationMediumType> getNotificationMedia() {
        return Collections.unmodifiableSet(notificationMedia);
    }

    /**
     * add a notification medium to this poi
     * @param notificationMedium
     * @throws ValidationException
     */
    public void addToNotificationMedia(NotificationMedium.NotificationMediumType notificationMedium) throws ValidationException {
        notNull(notificationMedium, "NotificationMedium can not be null");
        notificationMedia.add(notificationMedium);
    }

    /**
     * remove a notification medium from this poi
     * @param notificationMedium
     * @throws ValidationException
     */
    public void removeFromNotificationMedia(NotificationMedium.NotificationMediumType notificationMedium) throws ValidationException {
        validationAssert(notificationMedia.size() > 1, "Can't remove last element from NotificationMedia");
        validationAssert(notificationMedia.contains(notificationMedium), THIS_POI_DOES_NOT_CONTAIN + notificationMedium.toString());
        notificationMedia.remove(notificationMedium);
    }

    /**
     * @return True if the user wants to receive notifications for this point of interest, else this should be false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active The boolean that marks this point of interest as active or inactive.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Makes a deepcopy of properties from another PointOfInterest. The properties must be modifiable.
     * @param other: the poi from which to make a deepcopy of the underlining properties.
     */
    public void transferProperties(PointOfInterest other){
        setActive(other.isActive());
        setName(other.getName());
        setRadius(other.getRadius());
        this.notificationMedia = other.getNotificationMedia();
        this.notifyForEventTypes = other.getNotifyForEventTypes();
    }

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "address=" + address +
                ", name='" + name + '\'' +
                ", radius=" + radius +
                ", notifyForEventTypes=" + notifyForEventTypes +
                ", notificationMedia=" + notificationMedia +
                ", active=" + active +
                '}';
    }
}
