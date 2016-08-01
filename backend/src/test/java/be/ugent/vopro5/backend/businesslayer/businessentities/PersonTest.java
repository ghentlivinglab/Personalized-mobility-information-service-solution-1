package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Person;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import com.lambdaworks.crypto.SCryptUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Michael Weyns on 4/7/16.
 */
public class PersonTest extends AbstractBusinessEntityTest<Person> {

    private static Random random = new Random();
    private String password;

    @Test
    @Override
    public void testConstructor() {
        Person person = generatePerson();
        notNull(person);
    }

    @Before
    public void setUp() {
        password = SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1);
    }

    /**
     * Test construction without an id.
     */
    @Test(expected=ValidationException.class)
    public void testNoId() {
        new Person(
                null,
                generateEmailNotificationMediumObject(),
                password
        );
    }

    @Test(expected = ValidationException.class)
    public void testBlankEmail() {
        NotificationMedium emptyEmail = new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, "", true);
        new Person(UUID.randomUUID(), emptyEmail, password);
    }

    @Test(expected = ValidationException.class)
    public void testEmptyPassword() {
        new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), "");
    }

    @Test
    public void testSetEmail() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);

        String email = "test@example.com";
        person.setEmail(email);

        assertEquals(person.getEmail(), email);
    }

    @Test(expected = ValidationException.class)
    public void testSetInvalidEmail() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);

        String email = ".deia;@@dft";
        person.setEmail(email);
    }


    @Test(expected = ValidationException.class)
    public void testSetEmptyEmail() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);

        String email = "";
        person.setEmail(email);
    }

    @Test(expected = ValidationException.class)
    public void testSetNullEmail() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);

        String email = null;
        person.setEmail(email);
    }

    @Test
    public void testSetEmailIsValidated() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);

        person.setEmailisValidated();
        assertTrue(person.getEmailisValidated());
    }

    @Test
    public void testSetPassword() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);

        String pw = random.nextInt() + "";
        person.setPassword(pw);
        assertEquals(person.getPassword(), pw);
    }

    @Test(expected = ValidationException.class)
    public void testSetEmptyPassword() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);
        person.setPassword("");
    }

    @Test(expected = ValidationException.class)
    public void testSetNullPassword() {
        Person person = new Person(UUID.randomUUID(), generateEmailNotificationMediumObject(), password);
        person.setPassword(null);
    }

    @Test
    public void testGetEmailVerification() {
        NotificationMedium email = generateEmailNotificationMediumObject();
        Person person = new Person(UUID.randomUUID(), email, password);

        assertEquals(email.getPin(), person.getEmailVerification());
    }
}
