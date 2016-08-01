package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by evert on 21/03/16.
 */
public abstract class NotificationMediumMixin {

    NotificationMediumMixin(@JsonProperty("type") NotificationMedium.NotificationMediumType type,
                            @JsonProperty("value") String value,
                            @JsonProperty("validated") boolean validated,
                            @JsonProperty("pin") String pin
    ) {
    }

    @JsonProperty("type")
    public abstract NotificationMedium.NotificationMediumType getType();

    @JsonProperty("validated")
    public abstract boolean isValidated();

    @JsonProperty("value")
    public abstract String getValue();

    @JsonProperty("pin")
    public abstract String getPin();
}
