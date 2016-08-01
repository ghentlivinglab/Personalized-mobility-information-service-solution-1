package be.ugent.vopro5.backend.businesslayer.applicationfacade.event;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.EventController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.GenericEvent;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms.EventMatchingAlgorithm;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.EventDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michael Weyns on 1/03/2016.
 */
@RestController
@RequestMapping("/event/")
public class EventIndexController {

    @Autowired
    private DataAccessProvider dap;

    @Autowired
    private EventController eventController;

    private EventMatchingAlgorithm algorithm;

    /**
     * Returns events matching the filter constructed by the parameters below.
     *
     * @param userId (Optional) If supplied, the list will be filtered to only show relevant events for this user
     * @return A list of events, filtered by the above parameters.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<Event> getEvents(@RequestParam(value = "user_id", required = false) String userId) {
        DataAccessContext dac = dap.getDataAccessContext();
        EventDAO eventDAO = dac.getEventDAO();

        List<Event> events = eventDAO.listAllActive();
        if (userId != null && !userId.equals("")) {
            if (!SecurityContextHolder.getContext().getAuthentication().getCredentials().toString().equals(userId)) {
                throw new APIException.UnauthorizedException("user_id not allowed", Collections.singletonList("user_id"));
            }
            if (this.algorithm == null) {
                this.algorithm = new EventMatchingAlgorithm(dap);
            }
            User user = dac.getUserDAO().find(userId);
            ControllerCheck.notNull(user, User.class);
            Message.NotificationList notifications = algorithm.matchEvents(new Message.EventList(events), Collections.singletonList(user));
            events = notifications.stream().map(Message.Notification::getEvent).collect(Collectors.toList());
        }

        return events;
    }

    /**
     * Creates a new event.
     *
     * @param event Contains the attributes of the event that we want to create.
     * @return The created event.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Event createEvent(@Valid @RequestBody GenericEvent event) {
        event.setActive(true);
        eventController.createEvent(event);
        return event;
    }
}
