package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.util.*;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class RouteTest extends AbstractBusinessEntityTest<Route> {

    private static final int TEST_SIZE = 5;

    @Test
    @Override
    public void testConstructor() {
        Route route = generateRoute();
        notNull(route);
    }

    /**
     * Test Route's alternative construction.
     */
    @Test
    public void testConstructorWithoutUUID() {
        Route route = new Route(
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Collections.singleton(generateEventType())),
                generateNotificationMediaTypes(),
                new Random().nextBoolean());
        notNull(route);
    }

    /**
     * Test construction without an id.
     */
    @Test(expected=ValidationException.class)
    public void testNoId() {
        new Route(null,
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Collections.singleton(generateEventType())),
                generateNotificationMediaTypes(),
                new Random().nextBoolean());
    }

    /**
     *
     * Test construction with no waypoints.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNoWaypoints() {
        new Route(UUID.randomUUID(), new ArrayList<>(),generateTransportationType(),new HashSet<>(Arrays.asList(EventType.ACCIDENT_MAJOR)),generateNotificationMediaTypes(),true);
    }

    /**
     *
     * Test construction with TransportationType equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullTransportationType() {
        new Route(UUID.randomUUID(), new ArrayList<LatLon>(Arrays.asList(generateLatLon())),null,new HashSet<>(Arrays.asList(EventType.ACCIDENT_MAJOR)),generateNotificationMediaTypes(),true);
    }

    /**
     *
     * Test construction with an empty set of EventTypes.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testEmptyEventTypes() {
        new Route(UUID.randomUUID(), new ArrayList<LatLon>(Arrays.asList(generateLatLon())),generateTransportationType(),new HashSet<>(),generateNotificationMediaTypes(),true);
    }

    /**
     *
     * Test construction with NotificationMediumTypes equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullNotificationMediumTypes() {
        new Route(UUID.randomUUID(), new ArrayList<LatLon>(Arrays.asList(generateLatLon())),generateTransportationType(),new HashSet<>(),null,true);
    }

    /**
     * Test Route's toString method.
     */
    @Test
    public void testToString() {
        Route route = generateRoute();
        assertNotEquals(route.toString(), "");
    }

    /**
     * Test Route's getWayPoints method.
     */
    @Test
    public void testGetWaypoints() {
        List<LatLon> waypoints = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            waypoints.add(generateLatLon());
        }

        Route route = new Route(
                UUID.randomUUID(),
                waypoints,
                generateTransportationType(),
                new HashSet<>(Collections.singleton(generateEventType())),
                generateNotificationMediaTypes(),
                new Random().nextBoolean()
        );

        assertEquals(waypoints, route.getWaypoints());
    }

    /**
     * Test Route's getTransportationType method.
     */
    @Test
    public void testGetTransportationType() {
        TransportationType type = generateTransportationType();

        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                type,
                new HashSet<>(Collections.singleton(generateEventType())),
                generateNotificationMediaTypes(),
                new Random().nextBoolean()
        );

        assertEquals(type, route.getTransportationType());
    }

    /**
     * Test Route's getNotificationMedia method.
     */
    @Test
    public void testGetNotifyForEventTypes() {
        Set<EventType> eventTypes = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            EventType type;
            while (eventTypes.contains(type = generateEventType()));
            eventTypes.add(type);
        }

        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                eventTypes,
                generateNotificationMediaTypes(),
                new Random().nextBoolean()
        );

        assertEquals(eventTypes, route.getNotifyForEventTypes());
    }

    /**
     * Test Route's removeFromNotifyForEventTypes and addToNotifyForEventTypes methods.
     */
    @Test
    public void testRemoveAndAddEventType() {
        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Arrays.asList(EventType.ACCIDENT_MAJOR, EventType.ACCIDENT_MINOR)),
                generateNotificationMediaTypes(),
                new Random().nextBoolean()
        );

        int prevSize = route.getNotifyForEventTypes().size();
        EventType type = route.getNotifyForEventTypes().iterator().next();

        route.removeFromNotifyForEventTypes(type);
        assertEquals(prevSize - 1, route.getNotifyForEventTypes().size());
        assertFalse(route.getNotifyForEventTypes().contains(type));

        route.addToNotifyForEventTypes(type);
        assertEquals(prevSize, route.getNotifyForEventTypes().size());
        assertTrue(route.getNotifyForEventTypes().contains(type));
    }

    /**
     *
     * Test adding an EvenType equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testAddNullEventType() {
        generateRoute().addToNotifyForEventTypes(null);
    }

    /**
     *
     * Test removing an EventType that no longer belongs to the Route.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testRemoveNotContainedEventType() {
        Route route = generateRoute();
        EventType type = route.getNotifyForEventTypes().iterator().next();
        route.removeFromNotifyForEventTypes(type);
        route.removeFromNotifyForEventTypes(type);
    }

    /**
     *
     * Test removing the last EventType from the Route.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testRemoveLastEventType() {
        EventType type = generateEventType();
        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Collections.singleton(type)),
                generateNotificationMediaTypes(),
                new Random().nextBoolean()
        );

        route.removeFromNotifyForEventTypes(type);
    }

    /**
     * Test Route's getNotificationMedia method.
     */
    @Test
    public void testGetNotificationMedia() {
        Set<NotificationMedium.NotificationMediumType> mediaTypes = generateNotificationMediaTypes();

        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Collections.singleton(generateEventType())),
                mediaTypes,
                new Random().nextBoolean()
        );

        assertEquals(mediaTypes, route.getNotificationMedia());
    }

    /**
     * Test Route's removeFromNotificationMedia and addToNotificationMedia methods.
     */
    @Test
    public void testRemoveAndAddNotificationMedium() {
        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Collections.singleton(generateEventType())),
                new HashSet<>(Arrays.asList(NotificationMedium.NotificationMediumType.CELL_NUMBER,
                        NotificationMedium.NotificationMediumType.EMAIL)),
                new Random().nextBoolean()
        );

        int prevSize = route.getNotificationMedia().size();
        NotificationMedium.NotificationMediumType type = route.getNotificationMedia().iterator().next();

        route.removeFromNotificationMedia(type);
        assertEquals(prevSize - 1, route.getNotificationMedia().size());
        assertFalse(route.getNotificationMedia().contains(type));

        route.addToNotificationMedia(type);
        assertEquals(prevSize, route.getNotificationMedia().size());
        assertTrue(route.getNotificationMedia().contains(type));
    }

    /**
     *
     * Test adding a NotificationMediumType equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testAddNullNotificationMedium() {
        generateRoute().addToNotificationMedia(null);
    }

    /**
     *
     * Test removing an EventType that no longer belongs to the Route.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testRemoveNotContainedNotificationMedium() {
        Route route = generateRoute();
        NotificationMedium.NotificationMediumType type = route.getNotificationMedia().iterator().next();
        route.removeFromNotificationMedia(type);
        route.removeFromNotificationMedia(type);
    }

    /**
     *
     * Test removing the last NotificationMediumType from the Route.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testRemoveLastNotificationMedium() {
        NotificationMedium.NotificationMediumType type = generateNotificationMediaTypes().iterator().next();

        Route route = new Route(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singletonList(generateLatLon())),
                generateTransportationType(),
                new HashSet<>(Collections.singleton(generateEventType())),
                new HashSet<>(Collections.singleton(type)),
                new Random().nextBoolean()
        );

        route.removeFromNotificationMedia(type);
    }

    /**
     * Test Route's setActive method.
     */
    @Test
    public void testSetActive() {
        Route route = generateRoute();
        boolean active = new Random().nextBoolean();

        route.setActive(active);

        assertEquals(active, route.isActive());
    }

    /**
     * Test Route's transferProperties method.
     */
    @Test
    public void testTransferProperties() {
        Route route = generateRoute();
        Route other = generateRoute();

        route.transferProperties(other);

        assertEquals(other.getNotifyForEventTypes(), route.getNotifyForEventTypes());
        assertEquals(other.getNotificationMedia(), route.getNotificationMedia());
        assertEquals(other.isActive(), route.isActive());
    }
}
