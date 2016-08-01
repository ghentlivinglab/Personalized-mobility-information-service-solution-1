package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Jam;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.TransportationType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.EventDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertEventsEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;

/**
 * Created by Michael Weyns on 27/02/2016.
 */
public class MongoEventDAOTest {

    private static final int TEST_SIZE = 20;
    private MongoDataAccessContext dac;
    private EventDAO eventDAO;
    private MongoDataAccessProvider dap;

    /**
     * Set up the DataAccessProvider, DataAccessContext, and DAO.
     */
    @Before
    public void setUp() {
        dap = new MongoDataAccessProvider("testbackend");
        dac = (MongoDataAccessContext) dap.getDataAccessContext();
        eventDAO = dac.getEventDAO();
    }

    /**
     * Drop the current database, delete all the collections in the database.
     */
    @After
    public void tearDown() {
        dac.dropDB();
    }

    /**
     * Test the listAll method by generating a list of random Events
     * and comparing the results to the original list.
     */
    @Test
    public void testListAllEvents() throws Exception {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Event event = generateGenericEvent();
            eventDAO.insert(event);
            events.add(event);
        }

        List<Event> results = eventDAO.listAll();
        assertEquals(TEST_SIZE, results.size());

        // Comparator is used to ensure identical ordering in both result and original list.
        Comparator<Event> comparator = (e1, e2) -> e1.getIdentifier().compareTo(e2.getIdentifier());

        Collections.sort(results, comparator);
        Collections.sort(events, comparator);

        for (int i = 0; i < TEST_SIZE; i++) {
            assertEventsEqual(results.get(i), (events.get(i)));
        }
    }

    /**
     * Test the find method by generating a random Event, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testFindEventById() throws Exception {
        Event event = generateGenericEvent();
        assertNull(eventDAO.find(UUID.randomUUID().toString()));

        eventDAO.insert(event);
        Event result = eventDAO.find(event.getIdentifier().toString());

        assertNotNull(result);
        assertEventsEqual(event, result);
    }

    /**
     * Test the update method by generating a random Event, inserting it,
     * updating it, and comparing the original to the result.
     */
    @Test
    public void testUpdateEvent() throws Exception {
        Event event = generateGenericEvent();
        eventDAO.insert(event);

        Event randomEvent = generateGenericEvent();
        event.setActive(true);
        event.setDescription(randomEvent.getDescription());
        event.setLastEditTime(randomEvent.getLastEditTime());
        eventDAO.update(event);

        Event result = eventDAO.find(event.getIdentifier().toString());
        assertEventsEqual(event, result);

        while (event.getRelevantForTransportationTypes().size() <= 1) {
            TransportationType type;
            while (event.getRelevantForTransportationTypes().contains(type = generateTransportationType()));
            event.addToRelevantForTransportationTypes(type);
        }

        Set<TransportationType> transportationTypes = new HashSet<>(event.getRelevantForTransportationTypes());
        transportationTypes.stream().forEach(t -> removeAndAddTransportationType(event, t));

        if (event.getJams().isEmpty()) { event.addToJams(generateJam()); }
        Set<Jam> jams = new HashSet<>(event.getJams());
        jams.stream().forEach(j -> removeAndAddJam(event, j));
    }

    /**
     *
     * Remove a TransportationType from an Event, update it, and check whether the the result is correct.
     * Then do the same for addition.
     *
     * @param event The Event to which we add and from which we remove the TransportationType.
     * @param type The TransportationType which is to be added and removed.
     */
    private void removeAndAddTransportationType(Event event, TransportationType type) {
        int prevSize = event.getRelevantForTransportationTypes().size();

        event.removeFromRelevantForTransportationTypes(type);
        eventDAO.update(event);

        Event result = eventDAO.find(event.getIdentifier().toString());
        assertEquals(prevSize - 1, result.getRelevantForTransportationTypes().size());
        assertFalse(result.getRelevantForTransportationTypes().contains(type));
        assertEventsEqual(event, result);

        event.addToRelevantForTransportationTypes(type);
        eventDAO.update(event);

        result = eventDAO.find(event.getIdentifier().toString());
        assertEquals(prevSize, result.getRelevantForTransportationTypes().size());
        assertTrue(result.getRelevantForTransportationTypes().contains(type));
        assertEventsEqual(event, result);
    }

    /**
     *
     * Remove a Jam from an Event, update it, and check whether the the result is correct.
     * Then do the same for addition.
     *
     * @param event The Event to which we add and from which we remove the Jam.
     * @param jam The Jam which is to be added and removed.
     */
    private void removeAndAddJam(Event event, Jam jam) {
        int prevSize = event.getJams().size();

        event.removeFromJams(jam);
        eventDAO.update(event);

        Event result = eventDAO.find(event.getIdentifier().toString());
        List<String> results = result.getJams().stream().
                map(Jam::toString).collect(Collectors.toList());
        assertEquals(prevSize - 1, result.getJams().size());
        assertFalse(results.contains(jam.toString()));
        assertEventsEqual(event, result);

        event.addToJams(jam);
        eventDAO.update(event);

        result = eventDAO.find(event.getIdentifier().toString());
        results = result.getJams().stream().
                map(Jam::toString).collect(Collectors.toList());
        assertEquals(prevSize, result.getJams().size());
        assertTrue(results.contains(jam.toString()));
        assertEventsEqual(event, result);
    }

    /**
     * Test the insert method by generating a random Event, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testInsertEvent() throws Exception {
        Event event = generateGenericEvent();
        assertNull(eventDAO.find(UUID.randomUUID().toString()));

        eventDAO.insert(event);
        Event result = eventDAO.find(event.getIdentifier().toString());

        assertNotNull(result);
        assertEventsEqual(event, result);
    }

    /**
     * Test the delete method by generating and inserting two random Events,
     * deleting the first, and checking which are present in the results.
     */
    @Test
    public void testDeleteEvent() throws Exception {
        Event event1 = generateGenericEvent();
        Event event2 = generateGenericEvent();
        eventDAO.insert(event1);
        eventDAO.insert(event2);

        eventDAO.delete(event1);
        assertEquals(1, eventDAO.listAll().size());
        List<String> results = eventDAO.listAll().stream().
                map(Event::toString).collect(Collectors.toList());

        assertFalse(results.contains(event1.toString()));
        assertTrue(results.contains(event2.toString()));
    }

    /**
     *
     * Test the listAllActive method by generating a list of random Events, half of which are inactive,
     * and comparing the results to the original list of inactive events.
     *
     * @throws Exception
     */
    @Test
    public void testListAllActive() throws Exception {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Event event = generateGenericEvent();
            if (i % 2 == 0) {
                event.setActive(false);
            } else {
                events.add(event);
            }
            eventDAO.insert(event);
        }

        List<Event> results = eventDAO.listAllActive();
        assertEquals(TEST_SIZE / 2, results.size());

        // Comparator is used to ensure identical ordering in both result and original list.
        Comparator<Event> comparator = (e1, e2) -> e1.getIdentifier().compareTo(e2.getIdentifier());

        Collections.sort(results, comparator);
        Collections.sort(events, comparator);

        for (int i = 0; i < TEST_SIZE / 2; i++) {
            assertEventsEqual(results.get(i), (events.get(i)));
        }
    }

    /**
     *
     * Test the listAllActiveWaze method by generating a list of random Events, a quarter of which are active WazeEvents,
     * and comparing the results to the original list of active waze events.
     *
     * @throws Exception
     */
    @Test
    public void testListAllActiveWaze() throws Exception {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Event event;
            if (i % 2 == 0) {
                if (i % 4 == 0) {
                    event = generateWazeEvent();
                    events.add(event);
                } else {
                    event = generateWazeEvent();
                    event.setActive(false);
                }
            } else {
                event = generateGenericEvent();
            }
            eventDAO.insert(event);
        }

        List<WazeEvent> results = eventDAO.listAllActiveWaze();
        assertEquals(TEST_SIZE / 4, results.size());

        // Comparator is used to ensure identical ordering in both result and original list.
        Comparator<Event> comparator = (e1, e2) -> e1.getIdentifier().compareTo(e2.getIdentifier());

        Collections.sort(results, comparator);
        Collections.sort(events, comparator);

        for (int i = 0; i < TEST_SIZE / 4; i++) {
            assertEventsEqual(results.get(i), (events.get(i)));
        }
    }
}
