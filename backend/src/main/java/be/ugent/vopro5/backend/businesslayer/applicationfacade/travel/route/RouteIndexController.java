package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel.route;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 3/26/16.
 */
@RestController
@RequestMapping("/user/{userId}/travel/{travelId}/route")
public class RouteIndexController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * Get all the routes belonging to a travel
     * @param userId
     * @param travelId
     * @return a List of all the routes
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<Route> getRoutes(@PathVariable String userId, @PathVariable String travelId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
        ControllerCheck.notNull(travel, Travel.class);

        return new ArrayList<>(travel.getRoutes());
    }

    /**
     * Create a new route and add it to the travel
     * @param userId
     * @param travelId
     * @param route the new route
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Route createRoute(@PathVariable String userId, @PathVariable String travelId, @Valid @RequestBody Route route) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();
        TravelDAO travelDAO = context.getTravelDAO();
        RouteDAO routeDAO = context.getRouteDAO();

        concurrencyController.enter(userId);
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
            ControllerCheck.notNull(travel, Travel.class);
            travel.addRoute(route);
            travelDAO.update(travel);

            routeDAO.insert(route);
        } finally {
            concurrencyController.leave(userId);
        }

        return route;
    }
}
