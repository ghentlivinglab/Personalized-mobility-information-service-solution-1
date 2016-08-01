package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Address;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Set;

/**
 * Created by maarten on 19.03.16.
 */
public abstract class PointOfInterestMixIn {

     @JsonProperty
     private String name;
     @JsonProperty
     private Address address;
     @JsonProperty
     private int radius;
     @JsonProperty("notify_for_event_types")
     private Set<EventType> notifyForEventTypes;
     @JsonProperty("notify")
     @JsonSerialize(using = RouteMixin.NotificationMediumTypeSerializer.class)
     private Set<NotificationMedium.NotificationMediumType> notificationMedia;
     @JsonProperty("active")
     private boolean active;

     @JsonCreator
     public PointOfInterestMixIn(@JsonProperty("address") Address address,
                                 @JsonProperty("name") String name,
                                 @JsonProperty("radius") int radius,
                                 @JsonProperty("notify_for_event_types") Set<EventType> notifyForEventTypes,
                                 @JsonProperty("notify") @JsonDeserialize(using = RouteMixin.NotificationMediumTypeDeserializer.class) Set<NotificationMedium.NotificationMediumType> notificationMedia,
                                 @JsonProperty("active") boolean active) {}
}
