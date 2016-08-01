package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateLatLon;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTravel;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class LatLonTest extends AbstractImmutableEntityTest<LatLon> {

    @Test
    @Override
    public void testConstructor() {
        LatLon latLon = generateLatLon();
        notNull(latLon);
    }

    /**
     *
     * Test construction with an invalid latitude.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testWrongLatitude() {
        new LatLon(-91.0, ThreadLocalRandom.current().nextDouble(-180, 180));
    }

    /**
     *
     * Test construction with an invalid longitude.
     *
     * @throws ValidationException
     */
    @Test(expected=ValidationException.class)
    public void testWrongLongitude() {
        new LatLon(ThreadLocalRandom.current().nextDouble(-90, 90), 181.0);
    }

    /**
     * Test LatLon's getLatitude method.
     */
    @Test
    public void testGetLatitude() {
        double lat = ThreadLocalRandom.current().nextDouble(-90, 90);
        LatLon latLon = new LatLon(lat, ThreadLocalRandom.current().nextDouble(-180, 180));

        assertEquals(lat, latLon.getLat(), Math.ulp(1.0));
    }

    /**
     * Test LatLon's getLongitude method.
     */
    @Test
    public void testGetLongitude() {
        double lon = ThreadLocalRandom.current().nextDouble(-180, 180);
        LatLon latLon = new LatLon(ThreadLocalRandom.current().nextDouble(-90, 90), lon);

        assertEquals(lon, latLon.getLon(), Math.ulp(1.0));
    }

    @Test
    @Override
    public void testHashCodeUniqueness() {
        LatLon latLon1 = generateLatLon();
        LatLon latLon2 = generateLatLon();
        assertNotEquals(latLon1.hashCode(), latLon2.hashCode());
    }

    @Test
    @Override
    public void testEquals() {
        LatLon latLon1 = generateLatLon();
        assertEquals(latLon1, latLon1);

        LatLon latLon2 = generateLatLon();
        testNonEquality(latLon1, latLon2);

        notNullNotBlankString(latLon1);
        assertFalse(latLon1.equals(null));
        assertFalse(latLon1.equals(generateTravel()));
    }

    @Override
    protected void testNonEquality(LatLon latLon1, LatLon latLon2) {
        assertNotEquals(latLon1, latLon2);

        latLon2 = new LatLon(latLon1.getLat(),latLon2.getLon());
        assertNotEquals(latLon1, latLon2);

        latLon2 = new LatLon(latLon1.getLat(),latLon1.getLon());
        assertEquals(latLon1, latLon2);
    }

    /**
     * Test LatLon's toString method.
     */
    @Test
    public void testToString() {
        LatLon latLon = generateLatLon();
        assertNotEquals(latLon.toString(), "");
    }

    @Test
    public void testDeepCopy() {
        LatLon latLon = generateLatLon();
        LatLon deepCopy = latLon.deepCopy();
        assertFalse(latLon == deepCopy);
        assertEquals(latLon,deepCopy);
    }
}
