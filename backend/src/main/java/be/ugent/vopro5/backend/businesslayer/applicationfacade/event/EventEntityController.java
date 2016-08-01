package be.ugent.vopro5.backend.businesslayer.applicationfacade.event;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.EventController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.GenericEvent;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessObject;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Created by Michael Weyns on 29/02/2016.
 */
@RestController
@RequestMapping("/event/{eventId}/")
public class EventEntityController {

    @Autowired
    private DataAccessProvider dap;

    @Autowired
    private EventController eventController;

    /**
     * handles GET requests at "/event/{eventId}/"
     * @param eventId The ID of the event to get.
     * @return The event with the given id.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public Event getEvent(@PathVariable String eventId) {
        DataAccessContext dac = dap.getDataAccessContext();
        DataAccessObject<Event> eventDAO = dac.getEventDAO();

        Event event = eventDAO.find(eventId);
        ControllerCheck.notNull(event, Event.class);
        
        return event;
    }

    /**
     * handles PUT requests at "/event/{eventId}/"
     * @param eventId The ID of the event that needs to be updated.
     * @param event Contains the new properties of the event.
     * @return The updated event.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public Event updateEvent(@PathVariable String eventId, @Valid @RequestBody GenericEvent event) throws ValidationException {
        DataAccessContext dac = dap.getDataAccessContext();
        DataAccessObject<Event> eventDAO = dac.getEventDAO();

        Event prev = eventDAO.find(eventId);
        ControllerCheck.notNull(prev, Event.class);

        if (!prev.isActive()) {
            throw new APIException.DataConflictException("Event is already marked as non-active. You can't modify a non-active event.", Collections.singletonList("active"));
        }

        prev.transferProperties(event);
        eventController.updateEvent(prev);
        return prev;
    }
}
