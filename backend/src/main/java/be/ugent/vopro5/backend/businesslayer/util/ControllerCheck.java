package be.ugent.vopro5.backend.businesslayer.util;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Identifiable;
import be.ugent.vopro5.backend.businesslayer.util.constants.ControllerConstants;

import java.util.Collections;

/**
 * Created by anton on 3/26/16.
 */
public class ControllerCheck {

    /**
     * @param b
     * @param typeClass
     * @param <B>
     * @throws APIException
     */
    public static <B extends Identifiable> void notNull(B b, Class<B> typeClass) throws APIException{
        if (b == null) {
            throw new APIException.DataNotFoundException(ControllerConstants.notAssociated(typeClass.getName()), Collections.singletonList("id"));
        }
    }
}
