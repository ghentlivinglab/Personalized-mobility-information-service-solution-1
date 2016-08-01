package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.PointOfInterestDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertPoiEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generatePointOfInterest;
import static org.junit.Assert.*;

/**
 * Created by maarten on 23.03.16.
 */
public class MongoPointOfInterestDAOTest {

    private static final int TEST_SIZE = 20;
    private MongoDataAccessContext dac;
    private PointOfInterestDAO pointOfInterestDAO;
    private MongoDataAccessProvider dap;

    @Before
    public void setUp() {
        dap = new MongoDataAccessProvider("testbackend");
        dac = (MongoDataAccessContext) dap.getDataAccessContext();
        pointOfInterestDAO = dac.getPointOfInterestDAO();
    }

    /**
     * Drop the current database, delete all the collections in the database.
     */
    @After
    public void tearDown() {
        dac.dropDB();
    }

    /**
     * Tests the insertion of a PointOfInterest in the database
     */
    @Test
    public void testInsertPointOfInterest() {
        PointOfInterest poi = generatePointOfInterest();
        pointOfInterestDAO.insert(poi);
        PointOfInterest result = pointOfInterestDAO.find(poi.getIdentifier().toString());
        assertNotNull(result);
        assertPoiEqual(poi,result);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFindPointOfInterestById() throws Exception {
        PointOfInterest poi = generatePointOfInterest();
        assertNull(pointOfInterestDAO.find(UUID.randomUUID().toString()));

        pointOfInterestDAO.insert(poi);
        PointOfInterest result = pointOfInterestDAO.find(poi.getIdentifier().toString());
        assertNotNull(result);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testUpdatePointOfInterest() throws Exception {
        PointOfInterest poi = generatePointOfInterest();
        pointOfInterestDAO.insert(poi);

        PointOfInterest randomPoi = generatePointOfInterest();
        poi.setName(randomPoi.getName());
        poi.setActive(randomPoi.isActive());
        poi.setRadius(randomPoi.getRadius());
        pointOfInterestDAO.update(poi);

        if(poi.getNotificationMedia().size() > 1) {
            if(poi.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.CELL_NUMBER)) {
                addAndRemoveNotificationMedia(poi, NotificationMedium.NotificationMediumType.CELL_NUMBER);
            }
            if(poi.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL)) {
                addAndRemoveNotificationMedia(poi, NotificationMedium.NotificationMediumType.EMAIL);
            }

        }

        EventType eventType = null;
        for(EventType et :EventType.values()) {
            if(!poi.getNotifyForEventTypes().contains(et)) {
                eventType = et;
                break;
            }
        }

        PointOfInterest result;
        int prevSize = poi.getNotifyForEventTypes().size();
        if(!(prevSize == EventType.values().length)) {
            poi.addToNotifyForEventTypes(eventType);
            pointOfInterestDAO.update(poi);
            result = pointOfInterestDAO.find(poi.getIdentifier().toString());
            assertTrue(result.getNotifyForEventTypes().contains(eventType));
            assertEquals(result.getNotifyForEventTypes().size(),prevSize + 1);

            poi.removeFromNotifyFromEventTypes(eventType);
            pointOfInterestDAO.update(poi);
            result = pointOfInterestDAO.find(poi.getIdentifier().toString());
            assertEquals(result.getNotifyForEventTypes().size(),prevSize);
            assertFalse(result.getNotifyForEventTypes().contains(eventType));
        }
        result = pointOfInterestDAO.find(poi.getIdentifier().toString());
        assertPoiEqual(poi,result);

    }

    /**
     * @param poi point of interest where we add and remove the notification medium type from.
     * @param notificationMediumType the type of noticationmedium that we want to remove and add to the point of interest
     */
    private void addAndRemoveNotificationMedia(PointOfInterest poi, NotificationMedium.NotificationMediumType notificationMediumType) {
        int prevSize = poi.getNotificationMedia().size();
        poi.removeFromNotificationMedia(notificationMediumType);
        pointOfInterestDAO.update(poi);
        PointOfInterest result = pointOfInterestDAO.find(poi.getIdentifier().toString());
        assertTrue(result.getNotificationMedia().size() == prevSize - 1);
        assertFalse(result.getNotificationMedia().contains(notificationMediumType));

        poi.addToNotificationMedia(notificationMediumType);
        pointOfInterestDAO.update(poi);
        result = pointOfInterestDAO.find(poi.getIdentifier().toString());
        assertTrue(result.getNotificationMedia().size() == prevSize);
        assertTrue(result.getNotificationMedia().contains(notificationMediumType));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDeletePointOfInterest() throws Exception {
        int prevSize = pointOfInterestDAO.listAll().size();
        PointOfInterest poi1 = generatePointOfInterest();
        PointOfInterest poi2 = generatePointOfInterest();

        pointOfInterestDAO.insert(poi1);
        pointOfInterestDAO.insert(poi2);

        pointOfInterestDAO.delete(poi1);
        assertNull(pointOfInterestDAO.find(poi1.getIdentifier().toString()));

        assertEquals(prevSize + 1,pointOfInterestDAO.listAll().size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testListAllPointsOfInterest() throws Exception {
       assertEquals(pointOfInterestDAO.listAll().size(),0);

        List<PointOfInterest> poiList = new ArrayList<>();
        for(int i = 0; i < TEST_SIZE; i++) {
            PointOfInterest poi = generatePointOfInterest();
            poiList.add(poi);
            pointOfInterestDAO.insert(poi);
        }

        List<PointOfInterest> result = pointOfInterestDAO.listAll();
        assertEquals(result.size(),TEST_SIZE);

        List<UUID> poiIDs = poiList.stream().
                map(PointOfInterest::getIdentifier).collect(Collectors.toList());
        List<UUID> resultIDs = result.stream().
                map(PointOfInterest::getIdentifier).collect(Collectors.toList());

        Collections.sort(poiIDs);
        Collections.sort(poiIDs);

        for (int i = 0; i < TEST_SIZE; i++) {
            assertTrue(resultIDs.contains(poiIDs.get(i)));
        }

    }
}
