package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.RouteDAO;
import com.mongodb.client.MongoDatabase;

/**
 * RouteDAO for a Mongo database
 */
public class MongoRouteDAO extends MongoDAO<Route> implements RouteDAO {
    /**
     * create a new MongoRouteDAO
     * @param db the Mongo database to use
     */
    public MongoRouteDAO(MongoDatabase db) {
        super(db, "routes", Route.class);
    }
}
