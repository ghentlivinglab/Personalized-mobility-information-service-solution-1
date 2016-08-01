package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.City;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateCity;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTravel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Lukas on 21/03/2016.
 */
public class CityTest extends AbstractImmutableEntityTest<City> {

    private static final Random random = new Random();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Override
    public void testConstructor()  {
        City city = generateCity();
        notNull(city);
    }

    @Test(expected=ValidationException.class)
    public void testEmptyCityName()  {
        new City("", "9000");
    }


    @Test(expected=ValidationException.class)
    public void testEmptyPostalCode()  {
        new City("Gent", "");
    }

    @Test
    @Override
    public void testHashCodeUniqueness()  {
        City city = generateCity();
        City city2 = generateCity();
        assertNotEquals(city.hashCode(), city2.hashCode());
    }

    @Test
    public void gettersTest() {
        String name = random.nextInt() + "";
        String postalCode = random.nextInt() + "";
        City city = new City(name,postalCode);
        assertEquals(name,city.getName());
        assertEquals(postalCode,city.getPostalCode());
    }

    @Override
    protected void testNonEquality(City city, City t2)  {
        assertFalse(city.equals(t2));
    }

    @Test
    @Override
    public void testEquals()  {
        String name = random.nextInt() + "";
        String postalCode = random.nextInt() + "";

        City city = new City(name,postalCode);
        City city2 = generateCity();
        assertFalse(city.equals(generateTravel()));
        testNonEquality(city,null);
        testNonEquality(city,city2);
        city2 = new City(name,random.nextBoolean() + "");
        testNonEquality(city,city2);
        city2 = new City(random.nextBoolean() + "",postalCode);
        testNonEquality(city,city2);
        city2 = new City(name,postalCode);
        assertTrue(city.equals(city2));
    }

    @Test
    public void testToString() {
        assertNotNull(generateCity().toString());
    }


}
