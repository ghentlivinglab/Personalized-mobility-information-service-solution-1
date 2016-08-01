package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.datalayer.dataaccessinterface.*;
import com.mongodb.client.MongoDatabase;

/**
 * Created on 25/02/16.
 */

public class MongoDataAccessContext implements DataAccessContext {

    private final MongoDatabase db;

    /**
     *
     * Initialize the database.
     *
     * @param db The database to be initialized
     */
    public MongoDataAccessContext(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public TravelDAO getTravelDAO() {
        return new MongoTravelDAO(db, this);
    }

    @Override
    public PointOfInterestDAO getPointOfInterestDAO() {
        return new MongoPointOfInterestDAO(db);
    }

    @Override
    public RouteDAO getRouteDAO() {
        return new MongoRouteDAO(db);
    }

    @Override
    public OperatorDAO getOperatorDAO() {
        return new MongoOperatorDAO(db);
    }

    @Override
    public UserDAO getUserDAO() {
        return new MongoUserDAO(db, this);
    }

    @Override
    public EventDAO getEventDAO() {
        return new MongoEventDAO(db);
    }

    /**
     * Drop the current database, delete all the collections in the database
     */
    public void dropDB() { db.drop(); }

}
