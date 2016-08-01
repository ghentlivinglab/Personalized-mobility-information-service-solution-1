package be.ugent.vopro5.backend.datalayer.dataaccessinterface;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;

/**
 * Created on 3/03/16.
 */
public interface UserDAO extends DataAccessObject<User> {

    /**
     * Find the User in the database with the given email. Returns null if the object could not be found.
     *
     * @param email The email the user has to have
     * @return The user with the given email
     */
    User findByEmail(String email);
}
