package be.ugent.vopro5.backend.util;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by anton on 21.03.16.
 */
public class ObjectComparison {

    public static void assertUsersEqual(User user1, User user2){
        assertEquals(user1.getIdentifier(),user2.getIdentifier());

        assertTrue(user1.getMuteNotifications() == user2.getMuteNotifications());
        assertEquals(user1.getPassword(),user2.getPassword());

        assertPointOfInterestSetEquals(user1.getPointsOfInterest(),user2.getPointsOfInterest());
        assertTravelSetEquals(user1.getTravels(),user2.getTravels());
        // No equals yet fo
        assertNotificationMediumSetEquals(user1.getNotificationMedia(),user2.getNotificationMedia());

    }

    public static void assertOperatorsEqual(Operator operator1, Operator operator2) {
        assertEquals(operator1.getIdentifier(), operator2.getIdentifier());
        assertEquals(operator1.getPassword(), operator2.getPassword());
        assertEquals(operator1.getEmail(), operator2.getEmail());
    }

    private static void assertNotificationMediumsEquals(NotificationMedium notificationMedium, NotificationMedium notificationMedium1) {
        assertEquals(notificationMedium.isValidated(), notificationMedium1.isValidated());
        assertEquals(notificationMedium.getType(), notificationMedium1.getType());
        assertEquals(notificationMedium.getValue(), notificationMedium1.getValue());
    }

    public static void assertNotificationMediumSetEquals(Set<NotificationMedium> set1, Set<NotificationMedium> set2) {
        List<NotificationMedium> u1NotificationMediumList = new ArrayList<>(set1);
        List<NotificationMedium> u2NotificationMediumList = new ArrayList<>(set2);
        Collections.sort(u1NotificationMediumList, (notificationMedium, t1) -> notificationMedium.toString().compareTo(t1.toString()));
        Collections.sort(u2NotificationMediumList, (notificationMedium, t1) -> notificationMedium.toString().compareTo(t1.toString()));
        assertEquals(u1NotificationMediumList.size(), u2NotificationMediumList.size());
        for (int i = 0; i < u1NotificationMediumList.size(); i++) {
            assertNotificationMediumsEquals(u1NotificationMediumList.get(i), u2NotificationMediumList.get(i));
        }
    }

    public static void assertPointOfInterestSetEquals(Set<PointOfInterest> set1, Set<PointOfInterest> set2) {
        List<PointOfInterest> u1PointsOfInterest = new ArrayList<>(set1);
        List<PointOfInterest> u2PointsOfInterest = new ArrayList<>(set2);
        Collections.sort(u1PointsOfInterest, (p1, p2) -> p1.getIdentifier().compareTo(p2.getIdentifier()));
        Collections.sort(u2PointsOfInterest, (p1, p2) -> p1.getIdentifier().compareTo(p2.getIdentifier()));
        assertEquals(u1PointsOfInterest.size(), u2PointsOfInterest.size());
        for (int i = 0; i < u1PointsOfInterest.size(); i++) {
            assertPoiEqual(u1PointsOfInterest.get(i), u2PointsOfInterest.get(i));
        }
    }

    public static void assertTravelSetEquals(Set<Travel> set1, Set<Travel> set2) {
        List<Travel> u1Travels = new ArrayList<>(set1);
        List<Travel> u2Travels = new ArrayList<>(set2);
        Collections.sort(u1Travels, (p1, p2) -> p1.getIdentifier().compareTo(p2.getIdentifier()));
        Collections.sort(u2Travels, (p1, p2) -> p1.getIdentifier().compareTo(p2.getIdentifier()));
        assertEquals(u1Travels.size(), u2Travels.size());
        for (int i = 0; i < u1Travels.size(); i++) {
            assertTravelsEqual(u1Travels.get(i), u2Travels.get(i));
        }
    }

    public static void assertRoutesEqual(Route route1, Route route2) {
        assertEquals(route1.getIdentifier(), route2.getIdentifier());
        assertEquals(route1.getNotifyForEventTypes(), route2.getNotifyForEventTypes());
        assertEquals(route1.getNotificationMedia(), route2.getNotificationMedia());
        assertEquals(route1.getTransportationType(), route2.getTransportationType());
        assertEquals(route1.getWaypoints(), route2.getWaypoints());
    }

    public static void assertTravelsEqual(Travel travel1, Travel travel2){

        assertEquals(travel1.getIdentifier(),travel2.getIdentifier());
        assertEquals(travel1.getEndPoint(),travel2.getEndPoint());
        assertEquals(travel1.getStartPoint(),travel2.getStartPoint());
        assertEquals(travel1.getName(),travel2.getName());

        assertEquals(travel1.getRoutes().size(), travel2.getRoutes().size());

        assertSetOfRoutesEqual(travel1.getRoutes(),travel2.getRoutes());
    }

    public static void assertSetOfRoutesEqual(Set<Route> routeSet1, Set<Route> routeSet2) {
        List<Route> routes1 = new ArrayList<>(routeSet1);
        List<Route> routes2 = new ArrayList<>(routeSet2);

        Comparator<Route> comparator = (r1, r2) -> r1.getIdentifier().compareTo(r2.getIdentifier());
        Collections.sort(routes1, comparator);
        Collections.sort(routes2,comparator);

        for(int i = 0; i < routes1.size(); i++ ){
            assertRoutesEqual(routes1.get(i), routes2.get(i));
        }
    }

    public static void assertSetOfJamsEqual(Set<Jam> jamSet1, Set<Jam> jamSet2) {
        List<Jam> jams1 = new ArrayList<>(jamSet1);
        List<Jam> jams2 = new ArrayList<>(jamSet2);

        Comparator<Jam> comparator = (r1, r2) -> Integer.compare(r1.getDelay(),r2.getDelay());
        Collections.sort(jams1, comparator);
        Collections.sort(jams2,comparator);

        for(int i = 0; i < jams1.size(); i++ ){
            assertJamsEqual(jams1.get(i), jams2.get(i));
        }
    }

    public static void assertTimeIntervalConstraintsEqual(TimeIntervalConstraint tic1, TimeIntervalConstraint tic2){
        assertEquals(tic1.getStarttime(), tic2.getStarttime());
        assertEquals(tic1.getDuration().getSeconds(), tic2.getDuration().getSeconds());
    }

    public static void assertPoiEqual(PointOfInterest poi1, PointOfInterest poi2){
        assertEquals(poi1.getIdentifier(), poi2.getIdentifier());
        assertEquals(poi1.getNotifyForEventTypes(), poi2.getNotifyForEventTypes());
        assertEquals(poi1.getNotificationMedia(), poi2.getNotificationMedia());
        assertEquals(poi1.getAddress(), poi2.getAddress());
        assertEquals(poi1.getRadius(), poi2.getRadius());
        assertEquals(poi1.getName(), poi2.getName());
        assertEquals(poi1.isActive(),poi2.isActive());
    }

    public static void assertEventsEqual(Event event1, Event event2){
        assertEquals(event1.getIdentifier(), event2.getIdentifier());
        assertEquals(event1.getDescription(), event2.getDescription());
        assertEquals(event1.getEventType(), event2.getEventType());
        assertEquals(event1.getLastEditTime(), event2.getLastEditTime());
        assertEquals(event1.getLocation(), event2.getLocation());
        assertEquals(event1.getPublicationTime(), event2.getPublicationTime());
        assertEquals(event1.getRelevantForTransportationTypes(), event2.getRelevantForTransportationTypes());
        assertEquals(event1.getSource().getImage(), event2.getSource().getImage());
        assertEquals(event1.getSource().getName(), event2.getSource().getName());


        List<Jam> jams1 = new ArrayList<>(event1.getJams());
        List<Jam> jams2 = new ArrayList<>(event2.getJams());

        Comparator<Jam> comparator = (j1, j2) -> j1.toString().compareTo(j2.toString());
        Collections.sort(jams1, comparator);
        Collections.sort(jams2,comparator);

        for(int i = 0; i < jams1.size(); i++ ){
            assertJamsEqual(jams1.get(i), jams2.get(i));
        }
    }

    public static void assertGenericEventsJsonNodeEquals(JsonNode event1, JsonNode event2) {
        assertEquals(event1.get("id"),event2.get("id"));
        assertEquals(event1.get("coordinates"),event2.get("coordinates"));
        assertEquals(event1.get("active"),event2.get("active"));
        assertEquals(event1.get("publication_time"),event2.get("publication_time"));
        assertEquals(event1.get("last_edit_time"),event2.get("last_edit_time"));
        assertEquals(event1.get("description"),event2.get("description"));
        assertSetsAsJsonNodeEqual(event1.get("jams"),event2.get("jams"));
    }

    public static void assertUpdatedGenericEventIsValid(JsonNode after, JsonNode before ) {
        assertEquals(after.get("description"),before.get("description"));
        assertSetsAsJsonNodeEqual(after.get("jams"),before.get("jams"));
        assertSetsAsJsonNodeEqual(after.get("relevant_for_transportation_types"),before.get("relevant_for_transportation_types"));
    }

    public static void assertUpdatedRouteIsValid(JsonNode after, JsonNode before) {
        assertEquals(after.get("active"),before.get("active"));
        assertEquals(after.get("notify"),before.get("notify"));
        assertSetsAsJsonNodeEqual(after.get("notify_for_event_types"),before.get("notify_for_event_types"));
    }

    public static void assertUpdatedTravelIsValid(JsonNode after, JsonNode before) {
        assertEquals(after.get("is_arrival_time"),before.get("is_arrival_time"));
        assertEquals(after.get("name"),before.get("name"));
        assertEquals(after.get("time_interval"),before.get("time_interval"));
        assertEquals(after.get("recurring"),before.get("recurring"));
    }

    public static void assertUpdatedUserIsValid(JsonNode after, JsonNode before) {
        assertEquals(after.get("mute_notifications"),before.get("mute_notifications"));
        assertEquals(after.get("email"),before.get("email"));
      //  assertEquals(after.get("validated"),before.get("validated"));
    }

    public static void assertSetsAsJsonNodeEqual(JsonNode set1, JsonNode set2) {
        assertEquals(set1.size(),set2.size());

        boolean[] used = new boolean[set1.size()];

        for (int i = 0; i < set1.size(); i++) {
            boolean found = false;
            for (int j = 0; j < set2.size(); j++) {
                if (set1.get(i).equals(set2.get(j)) && !used[j]) {
                    found = true;
                    used[j] = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    public static void assertJamsEqual(Jam jam1, Jam jam2){
        assertEquals(jam1.getDelay(), jam2.getDelay());
        assertEquals(jam1.getPoints(), jam2.getPoints());
        assertTrue(new Float(jam1.getSpeed()).compareTo(jam2.getSpeed())==0);
    }

    public static void assertEventPublishersEqual(EventPublisher e1, EventPublisher e2) {
        assertEquals(e1.getClass(), e2.getClass());
        assertEquals(e1.getName(), e2.getName());
        assertEquals(e1.getImage(), e2.getImage());
    }
}
