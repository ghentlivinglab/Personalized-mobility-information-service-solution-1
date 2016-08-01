package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.TransportationType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class RouteMixin {

    RouteMixin(
            @JsonProperty("_id") UUID identifier,
            @JsonProperty("waypoints") List<LatLon> waypoints,
            @JsonProperty("transportation_type") TransportationType transportationType,
            @JsonProperty("notify_for_event_types") Set<EventType> notifyForEventTypes,
            @JsonProperty("notification_media") Set<NotificationMedium.NotificationMediumType> notificationMedia,
            @JsonProperty("active") boolean active
    ) {
    }

    @JsonProperty("_id")
    public abstract UUID getIdentifier();

    @JsonProperty("waypoints")
    public abstract List<LatLon> getWaypoints();

    @JsonProperty("transportation_type")
    public abstract TransportationType getTransportationType();

    @JsonProperty("notify_for_event_types")
    public abstract Set<EventType> getNotifyForEventTypes();

    @JsonProperty("notification_media")
    public abstract Set<NotificationMedium.NotificationMediumType> getNotificationMedia();

    @JsonProperty("active")
    public abstract boolean isActive();
}
