package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.TravelDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;


/**
 * Created by maarten on 03.03.16.
 */
@RestController
@RequestMapping("/user/{userId}/travel/")
public class TravelIndexController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * handles POST requests at "/user/{userId}/travel/"
     * @param userId The ID of the user who wants to create a travel.
     * @param travel Contains the properties of the travel that needs to be created.
     * @return The created travel.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Travel createTravel(@PathVariable String userId, @Valid @RequestBody Travel travel) {
        DataAccessContext context = dataAccessProvider.getDataAccessContext();
        UserDAO userDAO = context.getUserDAO();
        TravelDAO travelDAO = context.getTravelDAO();

        concurrencyController.enter(userId);
        try {
            User user = userDAO.find(userId);
            ControllerCheck.notNull(user, User.class);

            travelDAO.insert(travel);

            user.addTravel(travel);
            userDAO.update(user);
        } finally {
            concurrencyController.leave(userId);
        }

        return travel;
    }

    /**
     * handles GET requests at "/user/{userId}/travel/"
     * @param userId The ID of the user whose travels needs to be listed.
     * @return The list with all the travels of the user with ID userId.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public Set<Travel> getTravels(@PathVariable String userId) {
        UserDAO userDAO = dataAccessProvider.getDataAccessContext().getUserDAO();

        User user = userDAO.find(userId);
        ControllerCheck.notNull(user, User.class);

        return user.getTravels();
    }
}
