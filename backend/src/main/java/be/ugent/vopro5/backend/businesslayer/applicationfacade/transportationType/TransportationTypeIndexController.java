package be.ugent.vopro5.backend.businesslayer.applicationfacade.transportationType;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.TransportationType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by maarten on 30.03.16.
 */
@RestController
@RequestMapping("/transportationtype/")
public class TransportationTypeIndexController {

    /**
     * Handles GET requests at "/transportationtype/"
     * @return The list with all the different TranpsortationTypes.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<TransportationType> getTransportationTypes() {
        return Arrays.asList(TransportationType.values());
    }
}
