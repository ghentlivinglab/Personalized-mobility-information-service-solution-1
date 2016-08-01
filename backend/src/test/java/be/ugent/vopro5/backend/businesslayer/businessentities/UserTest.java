package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import com.lambdaworks.crypto.SCryptUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static be.ugent.vopro5.backend.util.ObjectComparison.*;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class UserTest extends AbstractBusinessEntityTest<User> {

    private static Random random = new Random();
    private Set<Travel> travels;
    private Set<PointOfInterest> pointsOfInterest;
    private String password;

    @Override
    public void testConstructor() {
        User user = generateUser();
        notNull(user);
    }

    @Before
    public void setUp() {
        travels = new HashSet<>();
        int numTravels = random.nextInt(14) + 1;
        for (int i = 0; i < numTravels; i++) {
            travels.add(generateTravel());
        }

        pointsOfInterest = generatePointOfInterestSet();

        password = SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1);
    }

    @Test
    public void testConstructorWithoutUUID() {
        Map<String,Boolean> validated = new HashMap<String,Boolean>();
        validated.put("email",random.nextBoolean());
        validated.put("cellNumber",random.nextBoolean());
        User user = new User(random.nextInt() +"@abc.com",random.nextInt() + "",random.nextBoolean(),password,validated);
        notNull(user);
    }

    @Test
    public void testConstructorWithoutUUIDAndCell() {
        Map<String, Boolean> validated = new HashMap<String, Boolean>();
        validated.put("email", random.nextBoolean());
        validated.put("cellNumber", false);
        User user = new User(random.nextInt() + "@abc.com", null, random.nextBoolean(), password, validated);
        notNull(user);
    }

    @Test(expected = ValidationException.class)
    public void testEmptyTravels() {
        new User(UUID.randomUUID(), generateEmailNotificationMediumObject(), generateNotificationMediaObjects(), null, pointsOfInterest, random.nextBoolean(), password);
    }


    @Test(expected = ValidationException.class)
    public void testEmptyPoI() {
        new User(UUID.randomUUID(), generateEmailNotificationMediumObject(), generateNotificationMediaObjects(), travels, null, random.nextBoolean(), password);
    }

    @Test
    public void testSetMuteNotifications() {
        User user = new User(UUID.randomUUID(), generateEmailNotificationMediumObject(), generateNotificationMediaObjects(), travels, pointsOfInterest, random.nextBoolean(), password);

        user.setMuteNotifications(true);
        assertEquals(user.getMuteNotifications(), true);

        user.setMuteNotifications(false);
        assertEquals(user.getMuteNotifications(), false);
    }

    /**
     * Test User's toString method.
     */
    @Test
    public void testToString() {
        User user = generateUser();
        assertNotNull(user.toString());
    }

    @Test
    public void testGetCellNumber() {
        NotificationMedium cellNumber = generateCellNumberNotificationMediumObject();
        Set<NotificationMedium> notificationMediumSet = new HashSet<>();
        notificationMediumSet.add(cellNumber);
        User user = new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                notificationMediumSet,
                travels,
                pointsOfInterest,
                random.nextBoolean(), password);
        assertEquals(user.getCellNumber(),cellNumber.getValue());
    }

    @Test
    public void testGetNullCellNumber() {
        Set<NotificationMedium> notificationMediumSet = new HashSet<>();
        User user = new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                notificationMediumSet,
                travels,
                pointsOfInterest,
                random.nextBoolean(), password);
        assertNull(user.getCellNumber());
    }

    @Test
    public void testTransferProperties() {
        User user = generateUser();
        User user2 = generateUser();
        user.transferProperties(user2);

        assertEquals(user.getMuteNotifications(),user2.getMuteNotifications());
        assertEquals(user.getPassword(),user2.getPassword());
        assertEquals(user.getEmail(),user2.getEmail());

        assertNotificationMediumSetEquals(user.getNotificationMedia(),user2.getNotificationMedia());
    }

    @Test
    public void testGetNotificationMedia() {
        NotificationMedium cellNumber = generateCellNumberNotificationMediumObject();
        Set<NotificationMedium> notificationMediumSet = new HashSet<>();
        notificationMediumSet.add(cellNumber);
        User user = new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                notificationMediumSet,
                travels,
                pointsOfInterest,
                random.nextBoolean(), password);
        assertNotificationMediumSetEquals(user.getNotificationMedia(),notificationMediumSet);
    }

    @Test(expected = ValidationException.class)
    public void testAddNullToNotificationMedia() {
        generateUser().addToNotificationMedia(null);
    }

    @Test
    public void testAddAndRemoveToNotificationMedia() {
        User user = generateUser();
        int prevSize = user.getNotificationMedia().size();
        NotificationMedium notificationMedium = generateCellNumberNotificationMedium();
        user.addToNotificationMedia(notificationMedium);
        assertEquals(user.getNotificationMedia().size(),prevSize+1);
        assertTrue(user.getNotificationMedia().contains(notificationMedium));

        user.removeFromNotificationMedia(notificationMedium);
        assertEquals(user.getNotificationMedia().size(),prevSize);
        assertFalse(user.getNotificationMedia().contains(notificationMedium));
    }

    @Test(expected = ValidationException.class)
    public void testRemoveMediumFromEmptySet() {
        new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                new HashSet<>(),
                travels,
                pointsOfInterest,
                random.nextBoolean(),
                password
        ).removeFromNotificationMedia(generateCellNumberNotificationMedium());
    }

    @Test
    public void testGetPointsOfInterest() {
        User user = new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                generateNotificationMediaObjects(),
                travels,
                pointsOfInterest,
                random.nextBoolean(),
                password
        );
        assertPointOfInterestSetEquals(user.getPointsOfInterest(),pointsOfInterest);
    }

    @Test(expected = ValidationException.class)
    public void testAddNullPointOfInterest() {
        generateUser().addPointOfInterest(null);
    }

    @Test
    public void testAddAndRemovePointOfInterest() {
        User user = generateUser();
        int prevSize = user.getPointsOfInterest().size();
        PointOfInterest pointOfInterest = generatePointOfInterest();
        user.addPointOfInterest(pointOfInterest);
        assertEquals(user.getPointsOfInterest().size(),prevSize+1);
        assertTrue(user.getPointsOfInterest().contains(pointOfInterest));

        user.removePointOfInterest(pointOfInterest);
        assertEquals(user.getPointsOfInterest().size(),prevSize);
        assertFalse(user.getPointsOfInterest().contains(pointOfInterest));
    }

    @Test(expected = ValidationException.class)
    public void testRemoveNotContainedPointOfInterest(){
        User user = generateUser();
        PointOfInterest pointOfInterest = user.getPointsOfInterest().iterator().next();
        user.removePointOfInterest(pointOfInterest);
        user.removePointOfInterest(pointOfInterest);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveFromEmptyPointsOfInterest() {
        new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                generateNotificationMediaObjects(),
                travels,
                new HashSet<>(),
                random.nextBoolean(),
                password
        ).removePointOfInterest(generatePointOfInterest());
    }

    @Test
    public void testGetEmailIsValidated() {
        boolean isValidated = random.nextBoolean();
        NotificationMedium notificationMedium = new NotificationMedium(
                NotificationMedium.NotificationMediumType.EMAIL,
                random.nextInt() + "@" + random.nextInt() + ".com",
                isValidated
                );
        User user = new User(UUID.randomUUID(),
                notificationMedium,
                generateNotificationMediaObjects(),
                travels,
                pointsOfInterest,
                random.nextBoolean(),
                password
        );

        assertEquals(user.getEmailisValidated(),isValidated);
    }

    @Test
    public void testGetTravels() {
        User user = new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                generateNotificationMediaObjects(),
                travels,
                pointsOfInterest,
                random.nextBoolean(),
                password
        );
        assertTravelSetEquals(user.getTravels(),travels);
    }

    @Test(expected = ValidationException.class)
    public void testAddNullTravel() {
        generateUser().addTravel(null);
    }

    @Test
    public void testAddAndRemoveTravel() {
        User user = generateUser();
        Travel travel = generateTravel();
        user.addTravel(travel);
        assertTrue(user.getTravels().contains(travel));
        user.removeTravel(travel);
        assertFalse(user.getTravels().contains(travel));
    }

    @Test(expected = ValidationException.class)
    public void testRemoveFromEmptyTravelSet() {
        new User(UUID.randomUUID(),
                generateEmailNotificationMediumObject(),
                generateNotificationMediaObjects(),
                new HashSet<>(),
                pointsOfInterest,
                random.nextBoolean(),
                password
        ).removeTravel(generateTravel());
    }

}
