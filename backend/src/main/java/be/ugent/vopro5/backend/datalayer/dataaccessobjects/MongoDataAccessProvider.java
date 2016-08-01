package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Created on 25/02/16.
 */
public class MongoDataAccessProvider implements DataAccessProvider {

    private final MongoDatabase db;

    /**
     * Set up the database.
     */
    public MongoDataAccessProvider(String dbName) {
        db = new MongoClient().getDatabase(dbName);
    }

    /**
     * Get a dataAccesContext
     * @return a new DataAccessContext
     */
    @Override
    public DataAccessContext getDataAccessContext() {
        return new MongoDataAccessContext(db);
    }
}