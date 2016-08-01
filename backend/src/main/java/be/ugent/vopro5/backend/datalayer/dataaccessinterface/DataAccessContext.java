package be.ugent.vopro5.backend.datalayer.dataaccessinterface;


/**
 * Created on 24/02/16.
 */
public interface DataAccessContext {

    /**
     * Retrieve an instance of an UserDAO
     *
     * @return The specific DAO
     */
    UserDAO getUserDAO();


    /**
     * Retrieve an instance of an EventDAO
     *
     * @return The specific DAO
     */
    EventDAO getEventDAO();


    /**
     * Retrieve an instance of a TravelDAO
     *
     * @return The specific DAO
     */
    TravelDAO getTravelDAO();

    /**
     * Retrieve an instance of a PointOfInterestDAO
     *
     * @return The specific DAO
     */
    PointOfInterestDAO getPointOfInterestDAO();

    /**
     * Retrieve an instance of a RouteDAO
     *
     * @return The specific DAO
     */
    RouteDAO getRouteDAO();

    /**
     * Retrieve an instance of a OperatorDAO
     *
     * @return The specific DAO
     */
    OperatorDAO getOperatorDAO();

}
