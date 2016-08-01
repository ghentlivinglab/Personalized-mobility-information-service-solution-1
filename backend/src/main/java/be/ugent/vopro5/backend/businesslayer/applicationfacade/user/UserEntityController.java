package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Person;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.GeneralUserVerifier;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.UserVerifier;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.businesslayer.util.constants.ControllerConstants;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Created on 28/02/16.
 */
@RestController
@RequestMapping("/user/{id}/")
public class UserEntityController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private GeneralUserVerifier verifier;

    @Autowired
    private ConcurrencyController concurrencyController;

    @Value("${admin.email}")
    private String adminEmail;

    /**
     * handles GET requests at /user/{id}/
     *
     * @param id The ID of the user we want to get.
     * @return The requested user.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public User getUser(@PathVariable String id) {
        UserDAO userDAO = dataAccessProvider.getDataAccessContext().getUserDAO();
        User user = userDAO.find(id);
        ControllerCheck.notNull(user, User.class);

        return user;
    }

    /**
     * handles DELETE requests at /user/{id}/
     *
     * @param id The ID of the user that needs to be deleted.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String id) {
        DataAccessContext dac = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = dac.getUserDAO();
        TravelDAO travelDAO = dac.getTravelDAO();
        PointOfInterestDAO pointOfInterestDAO = dac.getPointOfInterestDAO();
        RouteDAO routeDAO = dac.getRouteDAO();
        User user = userDAO.find(id);
        ControllerCheck.notNull(user, User.class);

        user.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::delete);
            travelDAO.delete(t);
        });
        user.getPointsOfInterest().forEach(pointOfInterestDAO::delete);
        userDAO.delete(user);
    }

    /**
     * handles PUT requests at /user/{id}/
     *
     * @param id   The ID of the user we want to update.
     * @param user Contains the new properties of the user that needs to be updated.
     * @return The updated user.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public User updateUser(@PathVariable String id, @Valid @RequestBody User user) throws ValidationException {
        UserDAO userDAO = dataAccessProvider.getDataAccessContext().getUserDAO();

        concurrencyController.enter(id);
        User prev;
        try {
            prev = userDAO.find(id);
            ControllerCheck.notNull(prev, User.class);

            User sameEmail = userDAO.findByEmail(user.getEmail());
            if (sameEmail != null && !sameEmail.getIdentifier().equals(prev.getIdentifier())) {
                throw new APIException.DataConflictException(ControllerConstants.EMAIL_ALREADY_IN_USE, Collections.singletonList("email"));
            }

            boolean emailChanged = !prev.getEmail().equals(user.getEmail());
            prev.transferProperties(user);
            userDAO.update(prev);

            if (emailChanged) {
                Person loggedInUser = ((Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                if (loggedInUser != null && loggedInUser.getEmail().equals(adminEmail)) {
                    prev.setEmailisValidated();
                    userDAO.update(prev);
                } else {
                    verifier.sendEmailVerification(user);
                }
            }
        } finally {
            concurrencyController.leave(id);
        }

        return prev;
    }
}
