package be.ugent.vopro5.backend.businesslayer.applicationfacade;

/**
 * Created by anton on 3/27/16.
 */
public class Endpoints {

    public static final String USER_INDEX_ENDPOINT = "/user/";
    public static final String EVENT_INDEX_ENDPOINT = "/event/";
    public static final String EVENTTYPE_INDEX_ENDPOINT = "/eventtype/";
    public static final String TRANSPORTATIONTYPE_INDEX_ENDPOINT = "/transportationtype/";
    public static final String OPERATOR_INDEX_ENDPOINT = "/operator/";

    /**
     * Get the path for the event entity endpoint with an accompanying id.
     *
     * @param id The event's ID
     * @return The path
     */
    public static String getEventEntityEndpoint(String id) {
        return EVENT_INDEX_ENDPOINT + id + "/";
    }

    /**
     * Get the path for the travel index endpoint with an accompanying user id.
     *
     * @param id The id belonging to the user
     * @return The path
     */
    public static String getPOIIndexEndpoint(String id) {
        return USER_INDEX_ENDPOINT + id + "/point_of_interest/";
    }

    /**
     * Get the path for a POI with accompanying user and POI ids.
     *
     * @param userId   The id belonging to the user
     * @param poiId The id belonging to the specific point of interest
     * @return The path
     */
    public static String getPOIEntityEndpoint(String userId, String poiId) {
        return getPOIIndexEndpoint(userId) + poiId + "/";
    }

    public static String getUserEntityEndpoint(String userId) {
        return USER_INDEX_ENDPOINT + userId + "/";
    }

    public static String getOperatorEntityEndpoint(String operatorId) {
        return OPERATOR_INDEX_ENDPOINT + operatorId + "/";
    }

    /**
     * Get the path for the travel index endpoint with an accompanying user id.
     *
     * @param id The id belonging to the user
     * @return The path
     */
    public static String getTravelIndexEndpoint(String id) {
        return USER_INDEX_ENDPOINT + id + "/travel/";
    }

    /**
     * Get the path for a travel with accompanying user and travel ids.
     *
     * @param userId   The id belonging to the user
     * @param travelId The id belonging to the specific travel
     * @return The path
     */
    public static String getTravelEntityEndpoint(String userId, String travelId) {
        return getTravelIndexEndpoint(userId) + travelId + "/";
    }

    public static String getRouteIndexEndpoint(String userId, String travelId) {
        return getTravelEntityEndpoint(userId, travelId) + "route/";
    }

    public static String getRouteEntityEndpoint(String userId, String travelId, String routeId) {
        return getRouteIndexEndpoint(userId, travelId) + routeId + "/";
    }

    public static String getVerifyEndpoint(String userId) {
        return USER_INDEX_ENDPOINT + userId + "/verify";
    }

    public static String getNotificationMediumEndpoint(String userId, String notificationMediumType) {
        return USER_INDEX_ENDPOINT + userId + "/notificationmedium/" + notificationMediumType + "/";
    }
}
