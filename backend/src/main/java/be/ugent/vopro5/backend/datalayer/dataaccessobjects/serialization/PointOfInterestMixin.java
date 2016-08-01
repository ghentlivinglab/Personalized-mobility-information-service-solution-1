package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Address;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

public abstract class PointOfInterestMixin {

    PointOfInterestMixin(
            @JsonProperty("_id") UUID identifier,
            @JsonProperty("address") Address address,
            @JsonProperty("name") String name,
            @JsonProperty("radius") int radius,
            @JsonProperty("notify_for_event_types") Set<EventType> notifyForEventTypes,
            @JsonProperty("notification_media") Set<NotificationMedium.NotificationMediumType> notificationMedia,
            @JsonProperty("active") boolean active
    ) {
    }

    @JsonProperty("_id")
    public abstract UUID getIdentifier();

    @JsonProperty("address")
    public abstract Address getAddress();

    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("radius")
    public abstract int getRadius();

    @JsonProperty("notify_for_event_types")
    public abstract Set<EventType> getNotifyForEventTypes();

    @JsonProperty("notification_media")
    public abstract Set<NotificationMedium.NotificationMediumType> getNotificationMedia();

    @JsonProperty("active")
    public abstract boolean isActive();

}
