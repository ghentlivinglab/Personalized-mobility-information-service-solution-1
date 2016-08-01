package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Created by thibault on 4/4/16.
 */
public abstract class OperatorMixin {
    @JsonProperty("_id")
    public abstract UUID getIdentifier();

    @JsonProperty("email")
    private NotificationMedium email;

    @JsonProperty("password")
    public abstract String getPassword();

    @JsonCreator
    OperatorMixin(@JsonProperty("_id") UUID identifier,
                  @JsonProperty("email") NotificationMedium email,
                  @JsonProperty("password") String password
    ) {
    }


}
