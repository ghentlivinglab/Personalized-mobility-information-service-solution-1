package be.ugent.vopro5.backend.datalayer.dataaccessobjects;


import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.RouteDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.TravelDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertUsersEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.*;


/**
 * Created by Lukas on 27/02/2016.
 */
public class MongoUserDAOTest {

    private static final int TEST_SIZE = 5;
    private MongoDataAccessProvider dap = null;
    private MongoDataAccessContext dac;
    private UserDAO userDAO;
    private TravelDAO travelDAO;
    private RouteDAO routeDAO;
    private PointOfInterestDAO pointOfInterestDAO;


    /**
     * Set up the DataAccessProvider, DataAccessContext, and DAO.
     */
    @Before
    public void setUp() {
        dap = new MongoDataAccessProvider("testbackend");
        dac = (MongoDataAccessContext) dap.getDataAccessContext();
        userDAO = dac.getUserDAO();
        pointOfInterestDAO = dac.getPointOfInterestDAO();
        routeDAO = dac.getRouteDAO();
        travelDAO = dac.getTravelDAO();
    }

    /**
     * Test the insert method by generating a random User, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testInsertUser() throws Exception {
        User user = generateUser();
        for(PointOfInterest poi: user.getPointsOfInterest()) {
            pointOfInterestDAO.insert(poi);
        }
        for(Travel travel: user.getTravels()) {
            for(Route route: travel.getRoutes()) {
                routeDAO.insert(route);
            }
            travelDAO.insert(travel);
        }
        userDAO.insert(user);

        User result = userDAO.find(user.getIdentifier().toString());
        assertNotNull(result);
        assertUsersEqual(user, result);
    }

    /**
     * Test the find method by generating a random User, inserting it, and
     * comparing the original to the result.
     */
    @Test
    public void testFindUserById() throws Exception {
        User user = generateUser();
        assertNull(userDAO.find(UUID.randomUUID().toString()));

        user.getPointsOfInterest().forEach(pointOfInterestDAO::insert);
        user.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::insert);
            travelDAO.insert(t);
        });
        userDAO.insert(user);
        User result = userDAO.find(user.getIdentifier().toString());

        assertNotNull(result);
    }

    @Test
    public void testFindUserByEmail() throws Exception {
        User result = userDAO.findByEmail("a@b.c");
        assertNull(result);

        User user = generateUser();
        user.getPointsOfInterest().forEach(pointOfInterestDAO::insert);
        user.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::insert);
            travelDAO.insert(t);
        });
        userDAO.insert(user);
        result = userDAO.findByEmail(user.getEmail());
        assertUsersEqual(user, result);
    }

    /**
     * Test the update method by generating a random User, inserting it,
     * updating it, and comparing the original to the result.
     */
    @Test
    public void testUpdateUser() throws Exception {
        User user = generateUser();
        user.getPointsOfInterest().forEach(pointOfInterestDAO::insert);
        user.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::insert);
            travelDAO.insert(t);
        });
        userDAO.insert(user);

        User randomUser = generateUser();
        user.setEmail(randomUser.getEmail());
        user.setMuteNotifications(randomUser.getMuteNotifications());
        user.setPassword(randomUser.getPassword());
        userDAO.update(user);

        User result = userDAO.findByEmail(user.getEmail());
        assertUsersEqual(user, result);

        removeAndAddTravel(user, (Travel)user.getTravels().toArray()[0]);

        Set<NotificationMedium> media = new HashSet<>(user.getNotificationMedia());
        media.stream().forEach(m -> removeAndAddNotificationMedium(user, m));
    }

    /**
     *
     * Remove a Travel from a User, update it, and check whether the the result is correct.
     * Then do the same for addition.
     *
     * @param user The User to which we add and from which we remove the Travel.
     * @param travel The Travel which is to be added and removed.
     */
    private void removeAndAddTravel(User user, Travel travel) {
        int prevLength = travelDAO.listAll().size();

        user.removeTravel(travel);
        travel.getRoutes().forEach(routeDAO::delete);
        travelDAO.delete(travel);
        userDAO.update(user);

        User result = userDAO.find(user.getIdentifier().toString());
        assertEquals(prevLength - 1, travelDAO.listAll().size());
        assertNull(travelDAO.find(travel.getIdentifier().toString()));
        assertUsersEqual(user, result);

        user.addTravel(travel);
        travel.getRoutes().forEach(routeDAO::insert);
        travelDAO.insert(travel);
        userDAO.update(user);

        result = userDAO.find(user.getIdentifier().toString());
        assertEquals(prevLength, travelDAO.listAll().size());
        assertNotNull(travelDAO.find(travel.getIdentifier().toString()));
        assertUsersEqual(user, result);
    }

    /**
     *
     * Remove a NotificationMedium from a User, update it, and check whether the the result is correct.
     * Then do the same for addition.
     *
     * @param user The User to which we add and from which we remove the NotificationMedium.
     * @param medium The NotificationMedium which is to be added and removed.
     */
    private void removeAndAddNotificationMedium(User user, NotificationMedium medium) {
        int prevSize = user.getNotificationMedia().size();

        user.removeFromNotificationMedia(medium);
        userDAO.update(user);

        User result = userDAO.find(user.getIdentifier().toString());
        List<String> results = result.getNotificationMedia().stream().
                map(NotificationMedium::toString).collect(Collectors.toList());
        assertEquals(prevSize - 1, result.getNotificationMedia().size());
        assertFalse(results.contains(medium.toString()));
        assertUsersEqual(user, result);

        user.addToNotificationMedia(medium);
        userDAO.update(user);

        result = userDAO.find(user.getIdentifier().toString());
        results = result.getNotificationMedia().stream().
                map(NotificationMedium::toString).collect(Collectors.toList());
        assertEquals(prevSize, result.getNotificationMedia().size());
        assertTrue(results.contains(medium.toString()));
        assertUsersEqual(user, result);
    }

    /**
     * Test the delete method by generating and inserting two random Users,
     * deleting the first, and checking which are present in the results.
     */
    @Test
    public void testDeleteUser() throws Exception {
        int prevSize = userDAO.listAll().size();
        User user = generateUser();
        User user2 = generateUser();
        user.getPointsOfInterest().forEach(pointOfInterestDAO::insert);
        user.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::insert);
            travelDAO.insert(t);
        });
        userDAO.insert(user);
        user2.getPointsOfInterest().forEach(pointOfInterestDAO::insert);
        user2.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::insert);
            travelDAO.insert(t);
        });
        userDAO.insert(user2);

        user.getPointsOfInterest().forEach(pointOfInterestDAO::delete);
        user2.getTravels().forEach(t -> {
            t.getRoutes().forEach(routeDAO::delete);
            travelDAO.delete(t);
        });
        userDAO.delete(user);
        assertEquals(prevSize + 1, userDAO.listAll().size());
    }

    /**
     * Test the listAll method by generating a list of random Users
     * and comparing the results to the original list.
     */
    @Test
    public void testListAllUsers() throws Exception {
        assertEquals(userDAO.listAll().size(), 0);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            User user = generateUser();
            users.add(user);
            user.getPointsOfInterest().forEach(pointOfInterestDAO::insert);
            user.getTravels().forEach(t -> {
                t.getRoutes().forEach(routeDAO::insert);
                travelDAO.insert(t);
            });
            userDAO.insert(user);
        }
        List<User> result = userDAO.listAll();
        assertEquals(result.size(), TEST_SIZE);

        List<UUID> userIDs = users.stream().map(User::getIdentifier).collect(Collectors.toList());
        List<UUID> resultIDs = result.stream().map(User::getIdentifier).collect(Collectors.toList());

        Collections.sort(userIDs);
        Collections.sort(resultIDs);

        for (int i = 0; i < TEST_SIZE; i++) {
            assertTrue(resultIDs.contains(userIDs.get(i)));
        }
    }

    /**
     * Drop the current database, delete all the collections in the database.
     */
    @After
    public void tearDown() {
        dac.dropDB();
    }
}
