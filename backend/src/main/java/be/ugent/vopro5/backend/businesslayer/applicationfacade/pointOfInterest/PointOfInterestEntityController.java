package be.ugent.vopro5.backend.businesslayer.applicationfacade.pointOfInterest;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by anton on 3/26/16.
 */
@RestController
@RequestMapping("/user/{userId}/point_of_interest/{pointOfInterestId}/")
public class PointOfInterestEntityController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * handles GET requests at "/user/{userId}/{pointOfInterestId}"
     *
     * @param userId: The id of the user the POI belongs to
     * @param pointOfInterestId: the id of the POI you want
     * @return the POI with the given ids
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public PointOfInterest getPointOfInterest(@PathVariable String userId, @PathVariable String pointOfInterestId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        PointOfInterest pointOfInterest = user.getPointsOfInterest().parallelStream().filter(p -> p.getIdentifier().toString().equals(pointOfInterestId)).findAny().orElse(null);
        ControllerCheck.notNull(pointOfInterest, PointOfInterest.class);

        return pointOfInterest;
    }

    /**
     * handles PUT requests at "/user/{userId}/{pointOfInterestId}"
     *
     * @param userId: the id of the user this POI belongs to
     * @param pointOfInterestId: the id of the POI
     * @param poi: The POI containing the updated values
     * @return POI form before the update
     * @throws ValidationException when the POI to update is not found
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public PointOfInterest updatePointOfInterest(@PathVariable String userId, @PathVariable String pointOfInterestId, @Valid @RequestBody PointOfInterest poi) throws ValidationException {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        PointOfInterestDAO pointOfInterestDAO = context.getPointOfInterestDAO();
        UserDAO userDAO = context.getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        PointOfInterest prev = user.getPointsOfInterest().parallelStream().filter(p -> p.getIdentifier().toString().equals(pointOfInterestId)).findAny().orElse(null);
        ControllerCheck.notNull(prev, PointOfInterest.class);

        prev.transferProperties(poi);
        pointOfInterestDAO.update(prev);

        return prev;
    }

    /**
     * handles DELETE requests at "/user/{userId}/{pointOfInterestId}"
     *
     * @param userId: the id of the user this POI belongs to
     * @param pointOfInterestId: the id of the POI
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deletePointOfInterest(@PathVariable String userId, @PathVariable String pointOfInterestId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();
        PointOfInterestDAO poiDAO = context.getPointOfInterestDAO();

        concurrencyController.enter(userId);
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            PointOfInterest pointOfInterest = user.getPointsOfInterest().parallelStream().filter(p -> p.getIdentifier().toString().equals(pointOfInterestId)).findAny().orElse(null);
            ControllerCheck.notNull(pointOfInterest, PointOfInterest.class);

            user.removePointOfInterest(pointOfInterest);
            userDAO.update(user);

            poiDAO.delete(pointOfInterest);
        } finally {
            concurrencyController.leave(userId);
        }
    }

}
