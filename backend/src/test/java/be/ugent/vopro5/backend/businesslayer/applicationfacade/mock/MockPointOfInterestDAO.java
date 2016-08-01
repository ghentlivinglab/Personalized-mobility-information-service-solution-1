package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;

/**
 * Created by thibault on 3/22/16.
 */
public class MockPointOfInterestDAO extends MockDAO<PointOfInterest> implements PointOfInterestDAO {
    public MockPointOfInterestDAO() {
        super(PointOfInterest.class);
    }
}
