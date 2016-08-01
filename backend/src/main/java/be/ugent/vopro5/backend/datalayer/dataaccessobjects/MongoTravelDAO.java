package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.RouteDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.TravelDAO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.util.UUID;

/**
 * Created on 1/03/16.
 */
public class MongoTravelDAO extends MongoDAO<Travel> implements TravelDAO {

    private final RouteDAO routeDAO;

    /**
     * Create a new MongoTravelDAO
     * @param db the Mongo database to use
     * @param dataAccessContext
     */
    public MongoTravelDAO(MongoDatabase db, DataAccessContext dataAccessContext) {
        super(db, "travels", Travel.class);
        routeDAO = dataAccessContext.getRouteDAO();
        getMapper().registerModule(new IdentifiableModule());
    }

    private class IdentifiableModule extends SimpleModule {
        IdentifiableModule() {
            addSerializer(Route.class, new JsonSerializer<Route>() {
                @Override
                public void serialize(Route value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    UUID id = value.getIdentifier();
                    gen.writeString(id.toString());
                }
            });

            addDeserializer(Route.class, new JsonDeserializer<Route>() {
                @Override
                public Route deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String id = p.getValueAsString();
                    return routeDAO.find(id);
                }
            });
        }
    }
}
