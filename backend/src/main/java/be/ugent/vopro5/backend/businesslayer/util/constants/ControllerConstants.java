package be.ugent.vopro5.backend.businesslayer.util.constants;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Identifiable;

/**
 * Created by lukas on 1/03/2016.
 */
public class ControllerConstants {

    public static final String STATUS = "status";
    public static final String FIELDS = "fields";
    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";

    public static <B extends Identifiable> String notAssociated(String className){
        return "No " + className.toLowerCase() + " associated with id";
    }
}
