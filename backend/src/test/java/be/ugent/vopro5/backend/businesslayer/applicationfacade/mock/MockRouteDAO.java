package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.RouteDAO;

/**
 * Created by thibault on 3/22/16.
 */
public class MockRouteDAO extends MockDAO<Route> implements RouteDAO {

    public MockRouteDAO() {
        super(Route.class);
    }
}
