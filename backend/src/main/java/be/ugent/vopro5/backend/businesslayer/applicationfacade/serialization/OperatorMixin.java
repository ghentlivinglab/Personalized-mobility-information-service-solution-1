package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by thibault on 4/4/16.
 */
public abstract class OperatorMixin {
    @JsonCreator
    public OperatorMixin(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "password", required = true) @JsonDeserialize(using = UserMixin.PasswordDeserializer.class) String password
    ) {}

    @JsonProperty("email")
    public abstract String getEmail();
}
