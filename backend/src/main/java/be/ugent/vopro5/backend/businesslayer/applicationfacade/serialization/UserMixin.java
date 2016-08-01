package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lambdaworks.crypto.SCryptUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by maarten on 21/03/16.
 */
public abstract class UserMixin {
    private static final String EMAIL= "email";

    @JsonIgnore
    private Set<NotificationMedium> notificationMedia;

    @JsonIgnore
    private Set<Travel> travels;

    @JsonIgnore
    private Set<PointOfInterest> pointsOfInterest;

    @JsonProperty("mute_notifications")
    private boolean muteNotifications;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonCreator
    public UserMixin(
            @JsonProperty(value = EMAIL, required = true) String email,
            @JsonProperty(value = "cell_number", required = false) String cellNumber,
            @JsonProperty(value = "mute_notifications", required = true) boolean muteNotifications,
            @JsonProperty(value = "password", required = true) @JsonDeserialize(using = PasswordDeserializer.class) String password,
            @JsonProperty(value = "validated", required = true) @JsonDeserialize(using = ValidatedDeSerializer.class) Map<String,Boolean> validated
    ) {}

    @JsonProperty("email")
    public abstract String getEmail();

    @JsonProperty("cell_number")
    public abstract String getCellNumber();

    @JsonIgnore
    public abstract boolean getEmailisValidated();

    @JsonIgnore
    public abstract boolean getEmailVerification();

    @JsonProperty("validated")
    @JsonSerialize(using = ValidatedSerializer.class)
    public abstract Set<NotificationMedium> getAllNotificationMedia();

    public static class PasswordDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String cleartext = p.getText();
            if (p.getText().equals("")) {
                throw new ValidationException("Password cannot be blank");
            }
            return SCryptUtil.scrypt(cleartext, 16384, 8, 1);
        }
    }

    private static class ValidatedSerializer extends JsonSerializer<Set<NotificationMedium>> {

        @Override
        public void serialize(Set<NotificationMedium> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            UserValidated validated = new UserValidated();
            for (NotificationMedium medium : value) {
                switch (medium.getType()) {
                    case CELL_NUMBER:
                        validated.cell_number |= medium.isValidated();
                        break;
                    case EMAIL:
                        validated.email |= medium.isValidated();
                        break;
                    default:
                }
            }
            gen.writeObject(validated);
        }

        private static class UserValidated {
            @JsonProperty("email")
            public boolean email;
            @JsonProperty("cell_number")
            public boolean cell_number;
        }
    }

    private static class ValidatedDeSerializer extends JsonDeserializer<Map<String,Boolean>> {

        @Override
        public Map<String, Boolean> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            Map<String,Boolean> validated = new HashMap<>();
            validated.put(EMAIL,node.get(EMAIL).asBoolean());
            validated.put("cellNumber",node.get("cell_number").asBoolean());
            return validated;
        }
    }
}
