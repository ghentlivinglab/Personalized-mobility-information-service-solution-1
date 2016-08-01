package be.ugent.vopro5.backend.businesslayer.applicationfacade.event;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anton on 3/5/16.
 */

@RestController
@RequestMapping("/eventtype/")
public class EventTypeIndexController {

    /**
     * Handles GET requests at "/eventtype/"
     * @return The list with all the different EventTypes.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<EventType> getEventTypes() {
        return Arrays.asList(EventType.values());
    }
}
