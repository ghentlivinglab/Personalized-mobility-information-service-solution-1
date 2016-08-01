package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel.route;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
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
 * Created by anton on 3/26/16.
 */
@RestController
@RequestMapping("/user/{userId}/travel/{travelId}/route/{routeId}")
public class RouteEntityController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * handles GET requests at "/user/{user_id}/travel/{travel_id}/route/{route_id}/"
     *
     * @param userId
     * @param travelId
     * @param routeId
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public Route getRoute(@PathVariable String userId, @PathVariable String travelId, @PathVariable String routeId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
        ControllerCheck.notNull(travel, Travel.class);

        Route route = travel.getRoutes().parallelStream().filter(p -> p.getIdentifier().toString().equals(routeId)).findAny().orElse(null);
        ControllerCheck.notNull(route, Route.class);

        return route;
    }

    /**
     * handles PUT requests at "/user/{user_id}/travel/{travel_id}/route/{route_id}/"
     * @param userId
     * @param travelId
     * @param routeId
     * @param route the route containing all the new values
     * @return the route from before the update
     * @throws ValidationException when the route to be updated can not be found
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public Route updateRoute(@PathVariable String userId, @PathVariable String travelId, @PathVariable String routeId, @Valid @RequestBody Route route) throws ValidationException {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        RouteDAO routeDAO = context.getRouteDAO();
        UserDAO userDAO = context.getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
        ControllerCheck.notNull(travel, Travel.class);

        Route prev = travel.getRoutes().parallelStream().filter(p -> p.getIdentifier().toString().equals(routeId)).findAny().orElse(null);
        ControllerCheck.notNull(prev, Route.class);
        prev.transferProperties(route);

        routeDAO.update(prev);
        return prev;
    }

    /**
     * handles DELETE requests at "/user/{user_id}/travel/{travel_id}/route/{route_id}/"
     * @param userId
     * @param travelId
     * @param routeId
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteRoute(@PathVariable String userId, @PathVariable String travelId, @PathVariable String routeId) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        RouteDAO routeDAO = context.getRouteDAO();
        TravelDAO travelDAO = context.getTravelDAO();
        UserDAO userDAO = context.getUserDAO();

        concurrencyController.enter(userId);
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            Travel travel = user.getTravels().parallelStream().filter(t -> t.getIdentifier().toString().equals(travelId)).findAny().orElse(null);
            ControllerCheck.notNull(travel, Travel.class);

            Route route = travel.getRoutes().parallelStream().filter(p -> p.getIdentifier().toString().equals(routeId)).findAny().orElse(null);
            ControllerCheck.notNull(route, Route.class);

            travel.removeRoute(route);
            travelDAO.update(travel);

            routeDAO.delete(route);
        } finally {
            concurrencyController.leave(userId);
        }
    }
}
