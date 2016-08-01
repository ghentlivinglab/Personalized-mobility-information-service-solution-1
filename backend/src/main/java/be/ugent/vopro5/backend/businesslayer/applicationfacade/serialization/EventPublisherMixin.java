package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventPublisher;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

/**
 * Created by thibault on 3/21/16.
 */
public abstract class EventPublisherMixin {
    @JsonCreator
    public static EventPublisher factory(
            @JsonProperty("type") EventPublisher.EventPublisherType type,
            @JsonProperty("name") String name,
            @JsonProperty("icon_url") String imageUrl
    ) throws IOException {
        return null;
    }

    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("icon_url")
    public abstract String getImage();

    @JsonProperty("type")
    public abstract EventPublisher.EventPublisherType getType();
}
