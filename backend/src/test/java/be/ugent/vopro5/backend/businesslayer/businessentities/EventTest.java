package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertSetOfJamsEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class EventTest extends AbstractBusinessEntityTest<Event> {

    private static final int TEST_SIZE = 2;

    @Test
    @Override
    public void testConstructor() {
        Event event = generateGenericEvent();
        notNull(event);
    }

    /**
     * Test Event's alternative constructor.
     */
    @Test
    public void testConstructorWithoutUUID() {
        GenericEvent event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );
        notNull(event);
    }

    /**
     * Test Event's toString method.
     */
    @Test
    public void testToString() {
        Event event = generateGenericEvent();
        assertNotEquals(event.toString(), "");
    }

    /**
     * Test construction without UUID.
     */
    @Test(expected=ValidationException.class)
    public void testNoId() {
        new GenericEvent(null, generateLatLon(), true, LocalDateTime.now(), LocalDateTime.now(), "some description", new HashSet<>(), EventType.ROAD_CLOSED_EVENT, generateEventPublisher(), new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with Location equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullLocation() {
        new GenericEvent(UUID.randomUUID(), null, true, LocalDateTime.now(), LocalDateTime.now(), "some description", new HashSet<>(), EventType.ROAD_CLOSED_EVENT, generateEventPublisher(), new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with PublicationTime equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullPublicationTime() {
        new GenericEvent(UUID.randomUUID(), generateLatLon(), true, null, LocalDateTime.now(), "some description", new HashSet<>(), EventType.ROAD_CLOSED_EVENT, generateEventPublisher(), new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with LastEditTime equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullLastEditTime() {
        new GenericEvent(UUID.randomUUID(), generateLatLon(), true, LocalDateTime.now(), null, "some description", new HashSet<>(), EventType.ROAD_CLOSED_EVENT, generateEventPublisher(), new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with Jams equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullJams() {
        new GenericEvent(UUID.randomUUID(), generateLatLon(), true, LocalDateTime.now(), LocalDateTime.now(), "some description", null, EventType.ROAD_CLOSED_EVENT, generateEventPublisher(), new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with EventType equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullEventType() {
        new GenericEvent(UUID.randomUUID(), generateLatLon(), true, LocalDateTime.now(), LocalDateTime.now(), "some description", new HashSet<>(), null, generateEventPublisher(), new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with EventPublisher equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullPublisher() {
        new GenericEvent(UUID.randomUUID(), generateLatLon(), true, LocalDateTime.now(), LocalDateTime.now(), "some description", new HashSet<>(), EventType.ROAD_CLOSED_EVENT, null, new HashSet<>(Collections.singletonList(generateTransportationType())));
    }

    /**
     *
     * Test construction with an empty set of TransportationTypes.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testEmptyTransportationTypes() throws ValidationException{
        new GenericEvent(UUID.randomUUID(), generateLatLon(), true, LocalDateTime.now(), LocalDateTime.now(), "some description", new HashSet<>(), EventType.ROAD_CLOSED_EVENT, null, new HashSet<>());
    }

    /**
     * Test Event's getLocation method.
     */
    @Test
    public void testGetLocation() {
        LatLon location = generateLatLon();

        Event event = new GenericEvent(
                location,
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );

        assertEquals(location, event.getLocation());
    }

    /**
     * Test Event's getPublicationTime method.
     */
    @Test
    public void testGetPublicationTime() {
        LocalDateTime publicationTime = LocalDateTime.now();

        Event event = new GenericEvent(
                UUID.randomUUID(),
                generateLatLon(),
                true,
                publicationTime,
                publicationTime.plusSeconds(1),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );

        assertEquals(publicationTime, event.getPublicationTime());
    }

    /**
     * Test Event's setLastEditTime method.
     */
    @Test
    public void testSetLastEditTime() {
        Event event = generateGenericEvent();
        LocalDateTime lastEditTime = LocalDateTime.now().plusSeconds(10);

        event.setLastEditTime(lastEditTime);

        assertEquals(lastEditTime, event.getLastEditTime());
    }

    /**
     *
     * Test setLastEditTime on an inactive Event.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testInactiveSetLastEditTime() {
        Event event = generateGenericEvent();
        LocalDateTime lastEditTime = LocalDateTime.now();

        event.setActive(false);
        event.setLastEditTime(lastEditTime);
    }

    /**
     *
     * Test setLastEditTime with LastEditTime equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullSetLastEditTime() {
        generateGenericEvent().setLastEditTime(null);
    }

    /**
     *
     * Test setDescription on an inactive Event
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testInactiveSetDescription() {
        Event event = generateGenericEvent();

        event.setActive(false);
        event.setDescription(new Random().nextLong() + "");
    }

    /**
     *
     * Test setDescription with a blank Description.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testNullSetDescription() {
        generateGenericEvent().setDescription(null);
    }

    /**
     * Test Event's setActive method.
     */
    @Test
    public void testSetActive() {
        Event event = generateGenericEvent();

        assertTrue(event.isActive());
        event.setActive(false);
        assertFalse(event.isActive());
    }

    /**
     * Test Event's getJams method.
     */
    @Test
    public void testGetJams() {
        Set<Jam> jams = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            jams.add(generateJam());
        }

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                jams,
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );

        List<String> results = event.getJams().stream().
                map(Jam::toString).collect(Collectors.toList());

        jams.stream().forEach(j -> assertTrue(results.contains(j.toString())));
    }

    /**
     * Test Event's removeFromJams and addToJams methods.
     */
    @Test
    public void testRemoveAndAddJam() {
        Set<Jam> jams = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            jams.add(generateJam());
        }

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                jams,
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );

        int prevSize = event.getJams().size();
        Jam jam = event.getJams().iterator().next();

        event.removeFromJams(jam);

        List<String> results = event.getJams().stream().
                map(Jam::toString).collect(Collectors.toList());
        assertEquals(prevSize - 1, event.getJams().size());
        assertFalse(results.contains(jam.toString()));

        event.addToJams(jam);

        results = event.getJams().stream().
                map(Jam::toString).collect(Collectors.toList());
        assertEquals(prevSize, event.getJams().size());
        assertTrue(results.contains(jam.toString()));
    }

    /**
     *
     * Test addToJams on an inactive Event.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testInactiveAddJam() {
        Event event = generateGenericEvent();

        event.setActive(false);
        event.addToJams(generateJam());
    }

    /**
     *
     * Test addToJams with Jam equal to null.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testAddNullJam() {
        generateGenericEvent().addToJams(null);
    }

    /**
     *
     * Test removeFromJams on an inactive Event.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testInactiveRemoveJam() {
        Event event = generateGenericEvent();
        Jam jam = generateJam();

        event.addToJams(jam);
        event.setActive(false);
        event.removeFromJams(jam);
    }

    /**
     * Test Event's getSource method.
     */
    @Test
    public void testGetSource() {
        EventPublisher source = generateEventPublisher();

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                source,
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );

        assertEquals(source.getName(), event.getSource().getName());
        assertEquals(source.getImage(), event.getSource().getImage());
        assertEquals(source.getType(), event.getSource().getType());
    }

    /**
     * Test Event's getEventType method.
     */
    @Test
    public void testGetEventType() {
        EventType type = generateEventType();

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                type,
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );

        assertEquals(type, event.getEventType());
    }

    /**
     * Test Event's getRelevantForTransportationTypes method.
     */
    @Test
    public void testGetRelevantForTransportationTypes() {
        Set<TransportationType> transportationTypes = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            TransportationType type;
            while (transportationTypes.contains(type = generateTransportationType()));
            transportationTypes.add(type);
        }

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                generateEventPublisher(),
                transportationTypes
        );

        transportationTypes.stream().forEach(t -> assertTrue(event.getRelevantForTransportationTypes().contains(t)));
    }

    /**
     * Test Event's removeAndAddTransportationType method.
     */
    @Test
    public void testRemoveAndAddTransportationType() {
        Set<TransportationType> transportationTypes = new HashSet<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            TransportationType type;
            while (transportationTypes.contains(type = generateTransportationType()));
            transportationTypes.add(type);
        }

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                generateEventPublisher(),
                transportationTypes
        );

        int prevSize = event.getRelevantForTransportationTypes().size();
        TransportationType type = event.getRelevantForTransportationTypes().iterator().next();

        event.removeFromRelevantForTransportationTypes(type);

        assertEquals(prevSize - 1, event.getRelevantForTransportationTypes().size());
        assertFalse(event.getRelevantForTransportationTypes().contains(type));

        event.addToRelevantForTransportationTypes(type);

        assertEquals(prevSize, event.getRelevantForTransportationTypes().size());
        assertTrue(event.getRelevantForTransportationTypes().contains(type));
    }

    /**
     *
     * Test addToRelevantForTransportationTypes on an inactive Event.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testInactiveAddTransportationType() {
        Event event = generateGenericEvent();

        event.setActive(false);
        event.addToRelevantForTransportationTypes(generateTransportationType());
    }

    /**
     *
     * Test removeFromRelevantForTransportationTypes on an inactive Event.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testInactiveRemoveTransportationType() {
        Event event = generateGenericEvent();

        event.setActive(false);
        event.removeFromRelevantForTransportationTypes(event.getRelevantForTransportationTypes().iterator().next());
    }

    /**
     *
     * Test removeFromRelevantForTransportationTypes on a TransportationType that no longer belongs to the Event.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testRemoveNotContainedTransportationType() {
        Event event = generateGenericEvent();
        TransportationType type = event.getRelevantForTransportationTypes().iterator().next();

        event.removeFromRelevantForTransportationTypes(type);
        event.removeFromRelevantForTransportationTypes(type);
    }

    /**
     *
     * Test removeFromRelevantForTransportationTypes on the last TransportationType in the set.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testRemoveLastTransportationType() {
        TransportationType type = generateTransportationType();

        Event event = new GenericEvent(
                generateLatLon(),
                new Random().nextLong() + "",
                new HashSet<>(Collections.singleton(generateJam())),
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(type))
        );

        event.removeFromRelevantForTransportationTypes(type);
    }

    /**
     * Test Event's transferProperties method.
     */
    @Test
    public void testTransferProperties() {
        Event event = generateGenericEvent();
        Event other = generateGenericEvent();

        event.transferProperties(other);

        assertEquals(event.isActive(), other.isActive());
        assertEquals(event.getDescription(), other.getDescription());
        assertNotEquals(event.getLastEditTime(), other.getLastEditTime());
        assertSetOfJamsEqual(event.getJams(),other.getJams());
        event.getRelevantForTransportationTypes().stream().forEach(t -> assertTrue(other.getRelevantForTransportationTypes().contains(t)));
    }
}
