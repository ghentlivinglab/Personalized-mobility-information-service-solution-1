package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Address;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.City;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class AddressTest extends AbstractImmutableEntityTest<Address> {

    private static Random random = new Random();

    @Test
    @Override
    public void testConstructor() {
        Address address = generateAddress();
        notNull(address);
    }

    @Test
    @Override
    public void testHashCodeUniqueness() {
        Address address = generateAddress();
        Address address2 = generateAddress();
        assertNotEquals(address.hashCode(), address2.hashCode());
    }

    @Test
    @Override
    public void testEquals() {
        Address address = generateAddress();
        assertEquals(address, address);

        Address address2 = generateAddress();
        testNonEquality(address, address2);
        assertFalse(address.equals(generateTravel()));
        assertFalse(address.equals(null));

        notNull(address);
    }

    @Test(expected=ValidationException.class)
    public void testBlankStreet()  {
        new Address("", "1", generateCity(), "BE", generateLatLon());
    }

    @Test(expected=ValidationException.class)
    public void testBlankHouseNumber()  {
        new Address("Street", "", generateCity(), "BE", generateLatLon());
    }

    @Test(expected=ValidationException.class)
    public void testNullCity()  {
        new Address("Street", "1", null, "BE", generateLatLon());
    }

    @Test(expected=ValidationException.class)
    public void testLengthCountry()  {
        new Address("Street", "1", generateCity(), "INV", generateLatLon());
    }

    @Test(expected = ValidationException.class)
    public void testNotNullCountry() {
        new Address("Street", "1", generateCity(), null, generateLatLon());
    }

    @Override
    protected void testNonEquality(Address address, Address address2)  {
        threeWayNotEquals(address, address2);

        address2 = new Address(address.getStreet(), address2.getHouseNumber(),
                address2.getCity(),address2.getCountry(),address2.getCoordinates());
        threeWayNotEquals(address, address2);

        address2 = new Address(address.getStreet(), address.getHouseNumber(),
                address2.getCity(),address2.getCountry(),address2.getCoordinates());
        threeWayNotEquals(address, address2);

        address2 = new Address(address.getStreet(), address.getHouseNumber(),
                address.getCity(),address2.getCountry(),address2.getCoordinates());
        threeWayNotEquals(address, address2);

        address2 = new Address(address.getStreet(), address.getHouseNumber(),
                address.getCity(),address.getCountry(),address2.getCoordinates());
        threeWayNotEquals(address, address2);

        address2 = new Address(address.getStreet(),address.getHouseNumber(),
                address.getCity(),"00",address2.getCoordinates()
                );
        threeWayNotEquals(address,address2);

        address2 = new Address(address.getStreet(), address.getHouseNumber(),
                address.getCity(),address.getCountry(),address.getCoordinates());
        twoWayEquals(address, address2);


    }

    /**
     * Test Address's toString method.
     */
    @Test
    public void testToString() {
        Address address = generateAddress();
        assertNotNull(address.toString());
    }

    @Test
    public void testGetterMethods() {
        City city = generateCity();
        String street = random.nextInt() + "";
        String country = (random.nextBoolean() + "").substring(0,2);
        LatLon latLon = generateLatLon();
        String houseNumber = random.nextInt() + "";
        Address address = new Address(street, houseNumber, city, country, latLon);

        assertEquals(city,address.getCity());
        assertEquals(latLon,address.getCoordinates());
        assertEquals(country,address.getCountry());
        assertEquals(houseNumber,address.getHouseNumber());
        assertEquals(street,address.getStreet());
    }
}
