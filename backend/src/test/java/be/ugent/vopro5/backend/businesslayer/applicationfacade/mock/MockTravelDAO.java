package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.TravelDAO;

/**
 * Created on 3/03/16.
 */
public class MockTravelDAO extends MockDAO<Travel> implements TravelDAO {

    /**
     * Inherit functionality from superclass.
     */
    public MockTravelDAO() {
        super(Travel.class);
    }
}
