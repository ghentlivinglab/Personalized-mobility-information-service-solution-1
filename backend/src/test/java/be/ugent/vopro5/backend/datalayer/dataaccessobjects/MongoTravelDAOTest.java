package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.RouteDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.TravelDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertTravelsEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTravel;
import static org.junit.Assert.*;

/**
 * Created by Lukas on 1/03/2016.
 */
public class MongoTravelDAOTest {

    private static final int TEST_SIZE = 20;
    private MongoDataAccessContext dac;
    private TravelDAO travelDAO;
    private MongoDataAccessProvider dap;
    private RouteDAO routeDAO;

    /**
     * Set up the DataAccessProvider, DataAccessContext, and DAO.
     */
    @Before
    public void setUp() {
        dap = new MongoDataAccessProvider("testbackend");
        dac = (MongoDataAccessContext) dap.getDataAccessContext();
        travelDAO = dac.getTravelDAO();
        routeDAO = dac.getRouteDAO();
    }

    /**
     * Test the listAll method by generating a list of random Travels
     * and comparing the results to the original list.
     */
    @Test
    public void testListAllTravel() throws Exception {
        assertEquals(0, travelDAO.listAll().size());

        List<Travel> travels = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            Travel travel = generateTravel();
            travel.getRoutes().forEach(routeDAO::insert);
            travels.add(travel);
            travelDAO.insert(travel);
        }

        List<Travel> results = travelDAO.listAll();
        assertEquals(TEST_SIZE, results.size());

        // Comparator is used to ensure identical ordering in both result and original list.
        Comparator<Travel> comparator = (t1, t2) -> t1.getIdentifier().compareTo(t2.getIdentifier());

        Collections.sort(results, comparator);
        Collections.sort(travels, comparator);

        for (int i = 0; i < TEST_SIZE; i++) {
            assertTravelsEqual(travels.get(i), results.get(i));
        }
    }

    /**
     * Test the insert method by generating a random Travel, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testInsertTravel() throws Exception {
        Travel travel = generateTravel();
        travel.getRoutes().forEach(routeDAO::insert);
        travelDAO.insert(travel);
        assertNotNull(travel.getIdentifier());
        assertNotNull(travelDAO.find(travel.getIdentifier().toString()));
    }

    /**
     * Test the find method by generating a random Travel, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testFindTravelById() throws Exception {
        Travel travel = generateTravel();
        assertNull(travelDAO.find(UUID.randomUUID().toString()));

        travel.getRoutes().forEach(routeDAO::insert);

        travelDAO.insert(travel);
        Travel result = travelDAO.find(travel.getIdentifier().toString());

        assertNotNull(result);
        assertTravelsEqual(travel, result);
    }

    /**
     * Test the update method by generating a random Travel, inserting it,
     * updating it, and comparing the original to the result.
     */
    @Test
    public void testUpdateTravel() throws Exception {
        Travel travel = generateTravel();
        travel.getRoutes().forEach(routeDAO::insert);
        travelDAO.insert(travel);

        travel.transferProperties(generateTravel());

        travelDAO.update(travel);

        Travel result = travelDAO.find(travel.getIdentifier().toString());
        assertTravelsEqual(travel, result);

        int numRoutes = routeDAO.listAll().size();
        Route toRemove = travel.getRoutes().iterator().next();
        travel.removeRoute(toRemove);
        routeDAO.delete(toRemove);
        travelDAO.update(travel);

        result = travelDAO.find(travel.getIdentifier().toString());
        assertTravelsEqual(travel, result);
        assertEquals(numRoutes - 1, routeDAO.listAll().size());
    }

    /**
     * Test the delete method by generating and inserting two random Travels,
     * deleting the first, and checking which are present in the results.
     */
    @Test
    public void testDeleteTravel() throws Exception {
        Travel travel = generateTravel();
        Travel travel2 = generateTravel();
        travel.getRoutes().forEach(routeDAO::insert);
        travel2.getRoutes().forEach(routeDAO::insert);
        travelDAO.insert(travel);
        travelDAO.insert(travel2);

        travel.getRoutes().forEach(routeDAO::delete);
        travelDAO.delete(travel);
        assertEquals(travelDAO.listAll().size(), 1);
        List<UUID> result = travelDAO.listAll().stream().
                map(Travel::getIdentifier).collect(Collectors.toList());

        assertFalse(result.contains(travel.getIdentifier()));
        assertTrue(result.contains(travel2.getIdentifier()));
    }

    /**
     * Drop the current database, delete all the collections in the database.
     */
    @After
    public void tearDown() {
        dac.dropDB();
    }
}
