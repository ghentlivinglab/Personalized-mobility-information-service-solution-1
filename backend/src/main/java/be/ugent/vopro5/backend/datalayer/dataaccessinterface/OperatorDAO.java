package be.ugent.vopro5.backend.datalayer.dataaccessinterface;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;

/**
 * Created by thibault on 4/4/16.
 */
public interface OperatorDAO extends DataAccessObject<Operator> {
    /**
     * Find the Operator in the database with the given email. Returns null if the object could not be found.
     *
     * @param email The email
     * @return The operator
     */
    Operator findByEmail(String email);
}
