package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class UserMixin {
    @JsonProperty("email")
    private NotificationMedium email;

    @JsonCreator
    UserMixin(@JsonProperty("_id") UUID identifier,
              @JsonProperty("email") NotificationMedium email,
              @JsonProperty("notification_media") Set<NotificationMedium> notificationMedia,
              @JsonProperty("travels") Set<Travel> travels,
              @JsonProperty("points_of_interest") Set<PointOfInterest> pointsOfInterest,
              @JsonProperty("mute_notifications") boolean muteNotifications,
              @JsonProperty("password") String password
    ) {
    }

    @JsonProperty("_id")
    public abstract UUID getIdentifier();

    @JsonProperty("mute_notifications")
    public abstract boolean getMuteNotifications();

    @JsonProperty("notification_media")
    public abstract Set<NotificationMedium> getNotificationMedia();

    @JsonProperty("travels")
    public abstract Set<Travel> getTravels();

    @JsonProperty("points_of_interest")
    public abstract Set<PointOfInterest> getPointsOfInterest();

    @JsonProperty("password")
    public abstract String getPassword();

    @JsonIgnore
    public abstract boolean getEmailisValidated();
}
