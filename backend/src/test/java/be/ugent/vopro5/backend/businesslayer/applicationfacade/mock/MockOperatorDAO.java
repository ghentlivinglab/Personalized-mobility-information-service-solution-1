package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.OperatorDAO;

/**
 * Created by thibault on 4/4/16.
 */
public class MockOperatorDAO extends MockDAO<Operator> implements OperatorDAO {
    public MockOperatorDAO() {
        super(Operator.class);
    }

    @Override
    public Operator findByEmail(String email) {
        return db.stream().map(this::readValue).filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
    }
}
