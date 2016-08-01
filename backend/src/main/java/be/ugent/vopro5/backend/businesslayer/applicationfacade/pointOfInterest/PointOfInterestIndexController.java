package be.ugent.vopro5.backend.businesslayer.applicationfacade.pointOfInterest;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * Created by anton on 3/26/16.
 */
@RestController
@RequestMapping("/user/{userId}/point_of_interest/")
public class PointOfInterestIndexController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * Create a new POI and add it to the user
     * @param userId the id of the user
     * @param poi the POI to add to the user
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public PointOfInterest createPOI(@PathVariable String userId, @Valid @RequestBody PointOfInterest poi) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();
        PointOfInterestDAO poiDAO = context.getPointOfInterestDAO();

        concurrencyController.enter(userId);
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            poiDAO.insert(poi);

            user.addPointOfInterest(poi);
            userDAO.update(user);
        } finally {
            concurrencyController.leave(userId);
        }

        return poi;
    }

    /**
     * Get all the POIs belonging to a user
     * @param userId
     * @return a Set containing al the POIs
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public Set<PointOfInterest> getPOIs(@PathVariable String userId) {
        UserDAO userDAO = dataAccessProvider.getDataAccessContext().getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        return user.getPointsOfInterest();
    }
}
