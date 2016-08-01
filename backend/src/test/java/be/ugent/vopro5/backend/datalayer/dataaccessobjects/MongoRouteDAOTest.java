package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.RouteDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertRoutesEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateEventType;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateRoute;
import static org.junit.Assert.*;

/**
 * Created by Michael Weyns on 3/23/16.
 */
public class MongoRouteDAOTest {
    private static final int TEST_SIZE = 20;
    private MongoDataAccessContext dac;
    private RouteDAO routeDAO;
    private MongoDataAccessProvider dap;

    /**
     * Set up the DataAccessProvider, DataAccessContext, and DAO.
     */
    @Before
    public void setUp() {
        dap = new MongoDataAccessProvider("test");
        dac = (MongoDataAccessContext) dap.getDataAccessContext();
        routeDAO = dac.getRouteDAO();
    }

    /**
     * Drop the current database, delete all the collections in the database.
     */
    @After
    public void tearDown() {
        dac.dropDB();
    }

    /**
     * Test the listAll method by generating a list of random Routes
     * and comparing the results to the original list.
     */
    @Test
    public void testListAllRoutes() throws Exception {
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Route route = generateRoute();
            routeDAO.insert(route);
            routes.add(route);
        }

        List<Route> results = routeDAO.listAll();
        assertEquals(TEST_SIZE, results.size());

        // Comparator is used to ensure identical ordering in both result and original list.
        Comparator<Route> comparator = (r1, r2) -> r1.getIdentifier().compareTo(r2.getIdentifier());

        Collections.sort(results, comparator);
        Collections.sort(routes, comparator);

        for (int i = 0; i < TEST_SIZE; i++) {
            assertRoutesEqual(results.get(i), routes.get(i));
        }
    }

    /**
     * Test the find method by generating a random Route, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testFindRouteById() throws Exception {
        Route route = generateRoute();
        assertNull(routeDAO.find(UUID.randomUUID().toString()));

        routeDAO.insert(route);
        Route result = routeDAO.find(route.getIdentifier().toString());

        assertNotNull(result);
        assertRoutesEqual(route, result);
    }

    /**
     * Test the update method by generating a random Route, inserting it,
     * updating it, and comparing the original to the result.
     */
    @Test
    public void testUpdateRoute() throws Exception {
        Route route = generateRoute();
        routeDAO.insert(route);

        route.setActive(!route.isActive());
        routeDAO.update(route);

        Route result = routeDAO.find(route.getIdentifier().toString());
        assertRoutesEqual(route, result);

        Set<NotificationMedium.NotificationMediumType> mediaTypes = new HashSet<>(route.getNotificationMedia());
        mediaTypes.stream().forEach(m -> removeAndAddNotificationMedium(route, m));

        while (route.getNotifyForEventTypes().size() <= 1) {
            EventType type;
            while (route.getNotifyForEventTypes().contains(type = generateEventType()));
            route.addToNotifyForEventTypes(type);
        }

        Set<EventType> eventTypes = new HashSet<>(route.getNotifyForEventTypes());
        eventTypes.stream().forEach(e -> removeAndAddEventType(route, e));
    }

    /**
     *
     * Remove a NotificationMediumType from a Route, update it, and check whether the the result is correct.
     * Then do the same for addition.
     *
     * @param route The Route to which we add and from which we remove the NotificationMediumType.
     * @param type The NotificationMediumType which is to be added and removed.
     */
    private void removeAndAddNotificationMedium(Route route, NotificationMedium.NotificationMediumType type) {
        int prevSize = route.getNotificationMedia().size();

        route.removeFromNotificationMedia(type);
        routeDAO.update(route);

        Route result = routeDAO.find(route.getIdentifier().toString());
        assertEquals(prevSize - 1, result.getNotificationMedia().size());
        assertFalse(result.getNotificationMedia().contains(type));
        assertRoutesEqual(route, result);

        route.addToNotificationMedia(type);
        routeDAO.update(route);

        result = routeDAO.find(route.getIdentifier().toString());
        assertEquals(prevSize, result.getNotificationMedia().size());
        assertTrue(result.getNotificationMedia().contains(type));
        assertRoutesEqual(route, result);
    }

    /**
     *
     * Remove an EventType from a Route, update it, and check whether the the result is correct.
     * Then do the same for addition.
     *
     * @param route The Route to which we add and from which we remove the EventType.
     * @param type The EventType which is to be added and removed.
     */
    private void removeAndAddEventType(Route route, EventType type) {
        int prevSize = route.getNotifyForEventTypes().size();

        route.removeFromNotifyForEventTypes(type);
        routeDAO.update(route);

        Route result = routeDAO.find(route.getIdentifier().toString());
        assertEquals(prevSize - 1, result.getNotifyForEventTypes().size());
        assertFalse(result.getNotifyForEventTypes().contains(type));
        assertRoutesEqual(route, result);


        route.addToNotifyForEventTypes(type);
        routeDAO.update(route);

        result = routeDAO.find(route.getIdentifier().toString());
        assertEquals(prevSize, result.getNotifyForEventTypes().size());
        assertTrue(result.getNotifyForEventTypes().contains(type));
        assertRoutesEqual(route, result);
    }

    /**
     * Test the insert method by generating a random Route, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testInsertRoute() throws Exception {
        Route route = generateRoute();

        routeDAO.insert(route);
        Route result = routeDAO.find(route.getIdentifier().toString());

        assertNotNull(result);
        assertRoutesEqual(route, result);
    }

    /**
     * Test the delete method by generating and inserting two random Routes,
     * deleting the first, and checking which are present in the results.
     */
    @Test
    public void testDeleteRoute() throws Exception {
        Route route1 = generateRoute();
        Route route2 = generateRoute();
        routeDAO.insert(route1);
        routeDAO.insert(route2);

        routeDAO.delete(route1);
        assertEquals(1, routeDAO.listAll().size());
        List<String> results = routeDAO.listAll().stream().
                map(Route::toString).collect(Collectors.toList());

        assertFalse(results.contains(route1.toString()));
        assertTrue(results.contains(route2.toString()));
    }

}
