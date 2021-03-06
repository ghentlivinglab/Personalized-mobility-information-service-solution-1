package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.TravelDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.UUID;

/**
 * Created on 3/03/16.
 */
public class MockUserDAO extends MockDAO<User> implements UserDAO {

    private final TravelDAO travelDAO;
    private final PointOfInterestDAO pointOfInterestDAO;

    /**
     *
     * Constructor for MockUserDAO. Add to functionality of superclass
     * by initializing a TravelDAO and registering a specialized mapping
     * module to the ObjectMapper.
     *
     * @param dac
     */
    public MockUserDAO(DataAccessContext dac) {
        super(User.class);
        travelDAO = dac.getTravelDAO();
        pointOfInterestDAO = dac.getPointOfInterestDAO();
        getMapper().registerModule(new IdentifiableModule());
    }

    @Override
    public User findByEmail(String email) {
        return db.stream().map(this::readValue).filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
    }

    private class IdentifiableModule extends SimpleModule {

        /**
         * Constructor for inner class. Add a custom
         * serializer and deserializer to (de)serialize only the ids of the
         * Identifiable objects.
         */
        IdentifiableModule() {
            addSerializer(Travel.class, new JsonSerializer<Travel>() {
                @Override
                public void serialize(Travel value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    UUID id = value.getIdentifier();
                    gen.writeString(id.toString());
                }
            });

            addSerializer(PointOfInterest.class, new JsonSerializer<PointOfInterest>() {
                @Override
                public void serialize(PointOfInterest value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    UUID id = value.getIdentifier();
                    gen.writeString(id.toString());
                }
            });

            addDeserializer(Travel.class, new JsonDeserializer<Travel>() {
                @Override
                public Travel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String id = p.getValueAsString();
                    return travelDAO.find(id);
                }
            });

            addDeserializer(PointOfInterest.class, new JsonDeserializer<PointOfInterest>() {
                @Override
                public PointOfInterest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String id = p.getValueAsString();
                    return pointOfInterestDAO.find(id);
                }
            });
        }
    }
}
