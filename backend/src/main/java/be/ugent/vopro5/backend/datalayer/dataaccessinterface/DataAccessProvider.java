package be.ugent.vopro5.backend.datalayer.dataaccessinterface;

/**
 * Created on 24/02/16.
 */
public interface DataAccessProvider {

    /**
     * Retrieve an instance of a DataAccessContext
     *
     * @return The DataAccessContext object
     */
    DataAccessContext getDataAccessContext();
}
