package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notNull;
import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.validationAssert;

/**
 * Created on 24/02/16.
 */
public class User extends Person {
    private static final String CANNOT_REMOVE_FROM_EMPTY_SET = "Cannot remove from an empty set";

    private Set<NotificationMedium> notificationMedia;

    private Set<Travel> travels;

    private Set<PointOfInterest> pointsOfInterest;

    private boolean muteNotifications;

    /**
     * create a new user. A user
     * @param identifier The UUID given to this user.
     * @param email The email address of this user.
     * @param notificationMedia The notification media of this user. The media through which the user wishes to be notified.
     * @param travels The travels of this user.
     * @param pointsOfInterest The points of interest of this user.
     * @param muteNotifications If true then this user does not wish to receive any notifications at all. If false, relevant
     *                          notifications can be sent to this user.
     * @param password
     * @throws ValidationException
     */
    public User(UUID identifier, NotificationMedium email, Set<NotificationMedium> notificationMedia, Set<Travel> travels,
                Set<PointOfInterest> pointsOfInterest, boolean muteNotifications, String password) throws ValidationException {
        super(identifier, email, password);
        this.notificationMedia = new HashSet<>(notificationMedia);
        notNull(travels, "Travels can not be null");
        this.travels = new HashSet<>(travels);
        notNull(pointsOfInterest, "PointsOfInterest can not be null");
        this.pointsOfInterest = new HashSet<>(pointsOfInterest);
        this.muteNotifications = muteNotifications;
    }

    /**
     * create a new user
     * @param email
     * @param cellNumber
     * @param muteNotifications
     * @param password
     * @param validated
     */
    public User(String email, String cellNumber, boolean muteNotifications, String password, Map<String, Boolean> validated) {
        this(UUID.randomUUID(),
                new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, email, validated.get("email")),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                muteNotifications,
                password
        );
        if (cellNumber != null) {
            notificationMedia.add(new NotificationMedium(NotificationMedium.NotificationMediumType.CELL_NUMBER,
                    cellNumber, validated.get("cellNumber")));
        }
    }

    public String getCellNumber() {
        for (NotificationMedium medium : this.notificationMedia) {
            if (medium.getType() == NotificationMedium.NotificationMediumType.CELL_NUMBER) {
                return medium.getValue();
            }
        }
        return null;
    }

    /**
     * @return If this user does not wish to be notified for events then this method should return false, otherwise it
     * should return true.
     */
    public boolean getMuteNotifications() {
        return muteNotifications;
    }

    /**
     * @param muteNotifications The boolean value to be set.
     */
    public void setMuteNotifications(boolean muteNotifications) {
        this.muteNotifications = muteNotifications;
    }

    /**
     * @return The list of travels of this user.
     */
    public Set<Travel> getTravels() {
        return Collections.unmodifiableSet(travels);
    }

    /**
     * remove a travel from this user
     * @param travel
     */
    public void removeTravel(Travel travel) {
        validationAssert(travels.size() > 0, CANNOT_REMOVE_FROM_EMPTY_SET);
        travels.remove(travel);
    }

    /**
     * add a travel to this user
     * @param travel
     */
    public void addTravel(Travel travel) {
        notNull(travel, "Travel can not be null");
        travels.add(travel);
    }

    /**
     * @return The list of points of interest of this user.
     */
    public Set<PointOfInterest> getPointsOfInterest() {
        return new HashSet<>(pointsOfInterest);
    }

    /**
     * add a point of interest to this user
     * @param poi
     */
    public void addPointOfInterest(PointOfInterest poi) {
        notNull(poi, "Point of interest can not be null");
        pointsOfInterest.add(poi);
    }

    /**
     * remove a point of interrest from this user
     * @param poi
     */
    public void removePointOfInterest(PointOfInterest poi) {
        validationAssert(pointsOfInterest.size() > 0, CANNOT_REMOVE_FROM_EMPTY_SET);
        validationAssert(pointsOfInterest.contains(poi), "PointsOfInterest does not contain the pointOfInterest to remove");
        pointsOfInterest.remove(poi);
    }


    /**
     * @return
     */
    public Set<NotificationMedium> getNotificationMedia() {
        return Collections.unmodifiableSet(notificationMedia);
    }

    /**
     * @return All notificationmedia, this includes the notificationMedium object email.
     */
    public Set<NotificationMedium> getAllNotificationMedia() {
        HashSet<NotificationMedium> newSet = new HashSet<>(notificationMedia);
        newSet.add(this.email);
        return Collections.unmodifiableSet(newSet);
    }

    /**
     * add a notification medium to this user
     * @param notificationMedium
     * @throws ValidationException
     */
    public void addToNotificationMedia(NotificationMedium notificationMedium) throws ValidationException {
        notNull(notificationMedium, "NotificationMedium can not be null");
        notificationMedia.add(notificationMedium);
    }

    /**
     * remove a notification medium from this user
     * @param notificationMedium
     * @throws ValidationException
     */
    public void removeFromNotificationMedia(NotificationMedium notificationMedium) throws ValidationException {
        validationAssert(notificationMedia.size() > 0, CANNOT_REMOVE_FROM_EMPTY_SET);
        notificationMedia.remove(notificationMedium);
    }

    /**
     * copy the properties from another user to this one
     * @param other
     * @throws ValidationException
     */
    public void transferProperties(User other) throws ValidationException {
        this.setMuteNotifications(other.getMuteNotifications());
        this.setPassword(other.getPassword());
        if (!this.getEmail().equals(other.getEmail())) {
            this.setEmail(other.getEmail());
        }
        this.notificationMedia = new HashSet<>(other.getNotificationMedia());
    }

    @Override
    public String toString() {
        return "User{" +
                "notificationMedia=" + notificationMedia +
                ", travels=" + travels +
                ", pointsOfInterest=" + pointsOfInterest +
                ", muteNotifications=" + muteNotifications +
                ", email=" + getEmail() +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}
