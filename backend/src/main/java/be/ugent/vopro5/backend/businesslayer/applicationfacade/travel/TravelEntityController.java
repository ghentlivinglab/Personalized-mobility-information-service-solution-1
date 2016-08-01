package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by maarten on 02.03.16.
 */
@RestController
@RequestMapping("/user/{userId}/travel/{travelId}/")
public class TravelEntityController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * handles  GET requests at /user/{userId}/travel/{travelId}/
     * @param userId The ID of the user whose travel needs to be obtained.
     * @param travelId The ID of the requested travel.
     * @return The requested travel.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public Travel getTravel(@PathVariable String userId, @PathVariable String travelId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
        ControllerCheck.notNull(travel, Travel.class);

        return travel;
    }

    /**
     * handles PUT requests at /user/{userId}/travel/{travelId}/
     * @param userId The ID of the user whose travel needs to be updated.
     * @param travelId The ID of the travel that needs to be updated
     * @param travel Contains the new properties of the travel that needs to be updated.
     * @return The updated Travel.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public Travel updateTravel(@PathVariable String userId, @PathVariable String travelId, @Valid @RequestBody Travel travel) throws ValidationException {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        TravelDAO travelDAO = context.getTravelDAO();
        UserDAO userDAO = context.getUserDAO();

        concurrencyController.enter(userId);
        Travel prev;
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            prev = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
            ControllerCheck.notNull(prev, Travel.class);

            prev.transferProperties(travel);

            travelDAO.update(prev);
        } finally {
            concurrencyController.leave(userId);
        }

        return prev;
    }

    /**
     * handles DELETE requests at /user/{userId}/travel/{travelId}/
     * @param userId The ID of the user whose travel needs to be deleted.
     * @param travelId The ID of the travel to delete.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteTravel(@PathVariable String userId, @PathVariable String travelId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        TravelDAO travelDAO = context.getTravelDAO();
        RouteDAO routeDAO = context.getRouteDAO();
        UserDAO userDAO = context.getUserDAO();

        concurrencyController.enter(userId);
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
            ControllerCheck.notNull(travel, Travel.class);

            user.removeTravel(travel);
            userDAO.update(user);

            travel.getRoutes().forEach(routeDAO::delete);
            travelDAO.delete(travel);
        } finally {
            concurrencyController.leave(userId);
        }
    }

}
