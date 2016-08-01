package be.ugent.vopro5.backend.businesslayer.businesscontrollers;

import akka.actor.ActorRef;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.EventDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by evert on 4/9/16.
 */
public class EventController {
    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ActorRef eventBroker;

    /**
     * Update an event in the database and notify the eventBroker
     * @param event
     */
    public void updateEvent(Event event) {
        EventDAO eventDAO = dataAccessProvider.getDataAccessContext().getEventDAO();
        eventDAO.update(event);

        Message.EventList eventList = new Message.EventList(Collections.singletonList(event));
        eventBroker.tell(eventList, null);
    }

    /**
     * Add a new event in the database and notify the eventBroker
     * @param event
     */
    public void createEvent(Event event) {
        EventDAO eventDAO = dataAccessProvider.getDataAccessContext().getEventDAO();
        eventDAO.insert(event);

        Message.EventList eventList = new Message.EventList(Collections.singletonList(event));
        eventBroker.tell(eventList, null);
    }

    /**
     * Add multiple events to the database and notify the eventBroker
     * @param events
     */
    public void createEvents(List<? extends Event> events) {
        List<Event> events1 = events.stream().map(e -> (Event) e).collect(Collectors.toList());

        EventDAO eventDAO = dataAccessProvider.getDataAccessContext().getEventDAO();
        events1.stream().forEach(eventDAO::insert);

        Message.EventList eventList = new Message.EventList(events1);
        eventBroker.tell(eventList, null);
    }
}
