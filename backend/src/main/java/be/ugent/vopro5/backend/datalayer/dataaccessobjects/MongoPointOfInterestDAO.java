package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;
import com.mongodb.client.MongoDatabase;

/**
 * PointOfInterestDAO for a Mongo database
 */
public class MongoPointOfInterestDAO extends MongoDAO<PointOfInterest> implements PointOfInterestDAO {
    /**
     * create a new MongoPointOfInterestDAO
     * @param db the Mongo database to use
     */
    public MongoPointOfInterestDAO(MongoDatabase db) {
        super(db, "points_of_interest", PointOfInterest.class);
    }
}
