package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import org.junit.Test;

import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by maarten on 27.03.16.
 */
public class NotificationMediumTest extends AbstractBusinessEntityTest<NotificationMedium> {

    private final static Random random = new Random();

    @Override
    public void testConstructor()  {
        notNull(generateEmailNotificationMediumObject());
        notNull(generateCellNumberNotificationMediumObject());
    }

    @Test
    public void testConstructorWithPin() {
        notNull(new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, random.nextInt() + "", random.nextBoolean(), random.nextInt() + ""));
        notNull(new NotificationMedium(NotificationMedium.NotificationMediumType.CELL_NUMBER, random.nextInt() + "", random.nextBoolean(), random.nextInt() + ""));
    }

    @Test
    public void testToString() {
        assertNotEquals(generateEmailNotificationMediumObject().toString(), "");
        assertNotEquals(generateCellNumberNotificationMediumObject().toString(), "");
    }

    @Test(expected = ValidationException.class)
    public void testBlankPin() {
        new NotificationMedium(generateNotificationMediaTypes().iterator().next(), random.nextInt() + "", random.nextBoolean(), "");
    }

    @Test(expected = ValidationException.class)
    public void testNullNotificationMediumType() {
        new NotificationMedium(null,
                random.nextInt() + "",
                random.nextBoolean()
        );
    }

    @Test(expected = ValidationException.class)
    public void testBlankValue() {
        new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL,
                "",
                random.nextBoolean()
        );
    }

    @Test
    public void testIsValidated() {
        NotificationMedium notificationMedium = generateCellNumberNotificationMedium();
        notificationMedium.validate();
        assertTrue(notificationMedium.isValidated());
    }

    @Test
    public void testGetType() {
        NotificationMedium email = generateEmailNotificationMediumObject();
        NotificationMedium cellNumber = generateCellNumberNotificationMediumObject();
        assertEquals(email.getType(), NotificationMedium.NotificationMediumType.EMAIL);
        assertEquals(cellNumber.getType(), NotificationMedium.NotificationMediumType.CELL_NUMBER);
    }

    @Test
    public void testSetValue() {
        NotificationMedium notificationMedium = generateCellNumberNotificationMediumObject();
        String value = random.nextInt() + "";
        notificationMedium.setValue(value);
        assertEquals(notificationMedium.getValue(),value);
    }

    @Test(expected = ValidationException.class)
    public void testSetBlankValue() {
        generateEmailNotificationMediumObject().setValue("");
    }

    @Test
    public void testEquals() {
        NotificationMedium medium1 = generateEmailNotificationMediumObject();
        assertEquals(medium1, medium1);

        NotificationMedium medium2 = generateCellNumberNotificationMedium();
        testNonEquality(medium1, medium2);

        notNullNotBlankString(medium1);
        assertFalse(medium1.equals(null));
        assertFalse(medium2.equals(generateCellNumberNotificationMedium()));
    }

    private void testNonEquality(NotificationMedium medium1, NotificationMedium medium2) {
        assertNotEquals(medium2, medium1);

        medium2 = new NotificationMedium(medium1.getType(),medium1.getValue(),false);
        assertEquals(medium1, medium2);
    }
}
