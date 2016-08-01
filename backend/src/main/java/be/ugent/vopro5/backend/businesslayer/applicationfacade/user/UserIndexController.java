package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.GeneralUserVerifier;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.UserVerifier;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.constants.ControllerConstants;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * Created on 24/02/16.
 */
@RestController
@RequestMapping("/user/")
public class UserIndexController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private GeneralUserVerifier verifier;

    /**
     * handles POST requests at /user/
     *
     * @param user Contains the properties of the user that we want to create.
     * @return The created user.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@Valid @RequestBody User user) {
        UserDAO userDAO = dataAccessProvider.getDataAccessContext().getUserDAO();

        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new APIException.DataConflictException(ControllerConstants.EMAIL_ALREADY_IN_USE, Collections.singletonList("email"));
        }

        verifier.sendEmailVerification(user);

        userDAO.insert(user);
        return user;
    }

    /**
     * handles GET requests at /user/
     *
     * @return If @param email is provided then a list with ONLY the user with the given email is returned.
     * In any other case the returned list will contain ALL the created users.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getUsers() {
        return dataAccessProvider.getDataAccessContext().getUserDAO().listAll();
    }
}
