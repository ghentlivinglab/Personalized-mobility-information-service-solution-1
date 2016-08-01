package be.ugent.vopro5.backend.businesslayer.applicationfacade;

/**
 * Created by anton on 3/27/16.
 */
public class Fields {

    // Common fields
    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String LINKS_FIELD = "links";
    public static final String REL_FIELD = "rel";
    public static final String HREF_FIELD = "href";

    // User fields
    public static final String EMAIL_FIELD = "email";
    public static final String CELL_NUMBER_FIELD = "cell_number";
    public static final String TRAVELS_FIELD = "travels";
    public static final String POINTS_OF_INTEREST_FIELD = "points_of_interest";
    public static final String MUTE_NOTIFICATIONS_FIELD = "mute_notifications";
    public static final String PASSWORD_FIELD = "password";

    // Travel fields
    public static final String TIME_INTERVAL = "time_interval";
    public static final String IS_ARRIVAL_TIME = "is_arrival_time";
    public static final String RECURRING = "recurring";
    public static final String STARTPOINT = "startpoint";
    public static final String ENDPOINT = "endpoint";

    // Event
    public static final String COORDINATES = "coordinates";
    public static final String DESCRIPTION = "description";
    public static final String ACTIVE = "active";
    public static final String PUBLICATION_TIME = "publication_time";
    public static final String LAST_EDIT_TIME = "last_edit_time";
    public static final String JAMS = "jams";
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String RELEVANT_FOR_TRANSPORTATION_TYPES = "relevant_for_transportation_types";

    // Route
    public static final String WAYPOINTS = "waypoints";
    public static final String TRANSPORTATION_TYPE = "transportation_type";
    public static final String NOTIFY_FOR_EVENT_TYPES = "notify_for_event_types";
    public static final String NOTIFY = "notify";

    // PointOfInterest
    public static final String ADDRESS = "address";
    public static final String RADIUS = "radius";

    // Error fields
    public static final String STATUS_FIELD = "status";
    public static final String FIELDS_FIELD = "fields";
    public static final String MESSAGE_FIELD = "message";

    // Other constants
    public static final String AT_SYMBOL = "@";
}
