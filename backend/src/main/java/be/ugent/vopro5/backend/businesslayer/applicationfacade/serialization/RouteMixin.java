package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.TransportationType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by thibault on 3/21/16.
 */
public abstract class RouteMixin {

    @JsonProperty("waypoints")
    private List<LatLon> waypoints;
    @JsonProperty("transportation_type")
    private TransportationType transportationType;
    @JsonProperty("notify_for_event_types")
    private Set<EventType> notifyForEventTypes;
    @JsonProperty("notify")
    @JsonSerialize(using = NotificationMediumTypeSerializer.class)
    private Set<NotificationMedium.NotificationMediumType> notificationMedia;
    @JsonProperty("active")
    private boolean active;

    @JsonCreator
    public RouteMixin(
            @JsonProperty("waypoints") List<LatLon> waypoints,
            @JsonProperty("transportation_type") TransportationType transportationType,
            @JsonProperty("notify_for_event_types") Set<EventType> notifyForEventTypes,
            @JsonProperty("notify") @JsonDeserialize(using = NotificationMediumTypeDeserializer.class) Set<NotificationMedium.NotificationMediumType> notificationMedia,
            @JsonProperty("active") boolean active
    ) {}

    public static class NotificationMediumTypeSerializer extends JsonSerializer<Set<NotificationMedium.NotificationMediumType>> {
        public NotificationMediumTypeSerializer() {
            // must be here, for Jackson
        }

        @Override
        public void serialize(Set<NotificationMedium.NotificationMediumType> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            boolean email = false;
            boolean cell_number = false;
            for (NotificationMedium.NotificationMediumType medium : value) {
                if (medium == NotificationMedium.NotificationMediumType.EMAIL) {
                    email = true;
                } else if (medium == NotificationMedium.NotificationMediumType.CELL_NUMBER) {
                    cell_number = true;
                }
            }
            gen.writeStartObject();
            gen.writeBooleanField("email", email);
            gen.writeBooleanField("cell_number", cell_number);
            gen.writeEndObject();
        }
    }

    public static class NotificationMediumTypeDeserializer extends JsonDeserializer<Set<NotificationMedium.NotificationMediumType>> {
        public NotificationMediumTypeDeserializer() {
        }

        @Override
        public Set<NotificationMedium.NotificationMediumType> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Set<NotificationMedium.NotificationMediumType> notificationMedia = new HashSet<>();
            NotifyObject notifyNode = p.readValueAs(NotifyObject.class);
            if(notifyNode.email) {
                notificationMedia.add(NotificationMedium.NotificationMediumType.EMAIL);
            }
            if(notifyNode.cell_number) {
                notificationMedia.add(NotificationMedium.NotificationMediumType.CELL_NUMBER);
            }

            return notificationMedia;
        }

        private static class NotifyObject {
            @JsonProperty("email")
            boolean email;

            @JsonProperty("cell_number")
            boolean cell_number;
        }
    }

}
