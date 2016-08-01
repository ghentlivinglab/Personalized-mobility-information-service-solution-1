package be.ugent.vopro5.backend.datalayer.dataaccessinterface;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;

import java.util.List;

/**
 * Created on 3/03/16.
 */
public interface EventDAO extends DataAccessObject<Event> {
    /**
     * List all currently active waze events
     * @return
     */
    List<WazeEvent> listAllActiveWaze();

    /**
     * List all currently active events
     * @return
     */
    List<Event> listAllActive();
}
