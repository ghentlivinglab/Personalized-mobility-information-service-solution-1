package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.datalayer.dataaccessinterface.*;

/**
 * Created on 28/02/16.
 */
public class MockDataAccessContext implements DataAccessContext {

    private UserDAO userDAO;
    private EventDAO eventDAO;
    private TravelDAO travelDAO;
    private RouteDAO routeDAO;
    private PointOfInterestDAO pointOfInterestDAO;
    private OperatorDAO operatorDAO;

    /**
     * Explicitly initialize all DAOs to null.
     */
    public MockDataAccessContext() {
        userDAO = null;
        eventDAO = null;
        travelDAO = null;
    }

    @Override
    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new MockUserDAO(this);
        }
        return userDAO;
    }

    @Override
    public EventDAO getEventDAO() {
        if (eventDAO == null) {
            eventDAO = new MockEventDAO();
        }
        return eventDAO;
    }

    @Override
    public TravelDAO getTravelDAO() {
        if (travelDAO == null) {
            travelDAO = new MockTravelDAO();
        }
        return travelDAO;
    }

    @Override
    public PointOfInterestDAO getPointOfInterestDAO() {
        if (pointOfInterestDAO == null) {
            pointOfInterestDAO = new MockPointOfInterestDAO();
        }
        return pointOfInterestDAO;
    }

    @Override
    public RouteDAO getRouteDAO() {
        if (routeDAO == null) {
            routeDAO = new MockRouteDAO();
        }
        return routeDAO;
    }

    @Override
    public OperatorDAO getOperatorDAO() {
        if (operatorDAO == null) {
            operatorDAO = new MockOperatorDAO();
        }
        return operatorDAO;
    }
}
