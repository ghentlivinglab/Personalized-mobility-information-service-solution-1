package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.EventDAO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 3/03/16.
 */
public class MockEventDAO extends MockDAO<Event> implements EventDAO {

    /**
     * Inherit functionality from superclass.
     */
    public MockEventDAO() {
        super(Event.class);
    }

    @Override
    public List<WazeEvent> listAllActiveWaze() {
        return db.stream().map(this::readValue).filter(Event::isActive).filter(e -> e instanceof WazeEvent).map(e -> (WazeEvent) e).collect(Collectors.toList());
    }

    @Override
    public List<Event> listAllActive() {
        return db.stream().map(this::readValue).filter(Event::isActive).collect(Collectors.toList());
    }
}