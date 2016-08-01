package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Address;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class PointOfInterestTest extends AbstractBusinessEntityTest<PointOfInterest> {
    private static Random random = new Random();

    @Override
    public void testConstructor()  {
        PointOfInterest poi = generatePointOfInterest();
        notNull(poi);
    }

    /**
     * Test PointOfInterest's alternative constructor.
     */
    @Test
    public void testConstructorWithoutUUID() {
        new PointOfInterest(
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    /**
     * Test PointOfInterest's toString method.
     */
    @Test
    public void testToString() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        assertNotNull(pointOfInterest.toString());
    }

    @Test(expected=ValidationException.class)
    public void testNoId()  {
        new PointOfInterest(
                null,
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test(expected=ValidationException.class)
    public void testNoAddress()  {
        new PointOfInterest(
                UUID.randomUUID(),
                null,
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test(expected=ValidationException.class)
    public void testBlankName()  {
        new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test(expected=ValidationException.class)
    public void testNoName()  {
        new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                null,
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test
    public void testRadiusZero()  {
        PointOfInterest poi = new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                0,
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                true
        );
        assertFalse(poi.isActive());
    }

    @Test(expected=ValidationException.class)
    public void testNegativeRadius()  {
        new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                -3,
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test(expected=ValidationException.class)
    public void testNoEventTypes()  {
        new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                null,
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test(expected=ValidationException.class)
    public void testEmptyEventTypes()  {
        new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                new HashSet<>(),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    @Test(expected=ValidationException.class)
    public void testNoNotificationMedia()  {
        new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                null,
                random.nextBoolean()
        );
    }

    @Test
    public void testSetActive() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        boolean active = pointOfInterest.isActive();
        pointOfInterest.setActive(!active);
        assertEquals(pointOfInterest.isActive(),!active);
    }

    @Test
    public void testSetName() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        String name = pointOfInterest.getName() + random.nextInt() + "";
        pointOfInterest.setName(name);
        assertEquals(name,pointOfInterest.getName());
    }

    @Test(expected = ValidationException.class)
    public void testSetBlankName() {
        generatePointOfInterest().setName("");
    }

    @Test
    public void testSetRadius() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        int radius = Math.abs(random.nextInt());
        pointOfInterest.setRadius(radius);
        assertEquals(radius,pointOfInterest.getRadius());
    }

    @Test(expected = ValidationException.class)
    public void testSetNegativeRadius() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        pointOfInterest.setRadius(-1);
    }

    @Test
    public void testGetAddress() {
        Address address = generateAddress();
        PointOfInterest pointOfInterest = new PointOfInterest(
                UUID.randomUUID(),
                address,
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
        assertEquals(address,pointOfInterest.getAddress());
    }

    @Test(expected = ValidationException.class)
    public void testAddNullToNotificationMedia() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        pointOfInterest.addToNotificationMedia(null);
    }

    @Test
    public void  testAddAndRemoveToNotificationMedia() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        int prevSize = pointOfInterest.getNotificationMedia().size();
        NotificationMedium.NotificationMediumType mediumType = (NotificationMedium.NotificationMediumType)
                pointOfInterest.getNotificationMedia().toArray()[0];
        pointOfInterest.removeFromNotificationMedia(mediumType);
        assertFalse(pointOfInterest.getNotificationMedia().contains(mediumType));
        assertEquals(prevSize -1, pointOfInterest.getNotificationMedia().size());

        pointOfInterest.addToNotificationMedia(mediumType);
        assertTrue(pointOfInterest.getNotificationMedia().contains(mediumType));
        assertEquals(prevSize,pointOfInterest.getNotificationMedia().size());
    }


    @Test(expected = ValidationException.class)
    public void testRemoveNotContainedNoticationMedia() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        pointOfInterest.removeFromNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);
        pointOfInterest.removeFromNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveFromEmptyNotificationMedia() {
        PointOfInterest pointOfInterest = new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                Collections.singleton(generateEventType()),
                new HashSet<>(),
                random.nextBoolean()
        );
        pointOfInterest.removeFromNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);
    }

    @Test
    public void testAddAndRemoveNotifyForEventTypes() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        int prevSize = pointOfInterest.getNotifyForEventTypes().size();
        EventType eventType = (EventType) pointOfInterest.getNotifyForEventTypes().toArray()[0];
        pointOfInterest.removeFromNotifyFromEventTypes(eventType);
        assertFalse(pointOfInterest.getNotifyForEventTypes().contains(eventType));
        assertEquals(prevSize - 1, pointOfInterest.getNotifyForEventTypes().size());

        pointOfInterest.addToNotifyForEventTypes(eventType);
        assertTrue(pointOfInterest.getNotifyForEventTypes().contains(eventType));
        assertEquals(prevSize,pointOfInterest.getNotifyForEventTypes().size());
    }

    @Test(expected = ValidationException.class)
    public void testAddNullToNotifyForEventTypes() {
        generatePointOfInterest().addToNotifyForEventTypes(null);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveNotContainedEventType() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        EventType type = (EventType) pointOfInterest.getNotifyForEventTypes().toArray()[0];
        pointOfInterest.removeFromNotifyFromEventTypes(type);
        pointOfInterest.removeFromNotifyFromEventTypes(type);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveFromEmptyNotifyForEventTypes() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        EventType eventType = null;
        for(int i=0; i < pointOfInterest.getNotifyForEventTypes().size() -1; i++) {
            eventType = (EventType) pointOfInterest.getNotifyForEventTypes().toArray()[0];
            pointOfInterest.removeFromNotifyFromEventTypes(eventType);
        }
        pointOfInterest.removeFromNotifyFromEventTypes((EventType)pointOfInterest.getNotifyForEventTypes().toArray()[0]);
    }

    @Test
    public void testTransferProperties() {
        PointOfInterest pointOfInterest = generatePointOfInterest();
        PointOfInterest pointOfInterest2 = generatePointOfInterest();
        pointOfInterest.transferProperties(pointOfInterest2);
        assertEquals(pointOfInterest.isActive(),pointOfInterest2.isActive());
        assertEquals(pointOfInterest.getRadius(),pointOfInterest2.getRadius());
        assertEquals(pointOfInterest.getName(),pointOfInterest2.getName());
        assertEquals(pointOfInterest.getNotifyForEventTypes(),pointOfInterest2.getNotifyForEventTypes());
        assertEquals(pointOfInterest.getNotificationMedia(),pointOfInterest2.getNotificationMedia());
    }

}
