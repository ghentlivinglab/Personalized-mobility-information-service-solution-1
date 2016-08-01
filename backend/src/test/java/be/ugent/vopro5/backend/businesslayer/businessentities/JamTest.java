package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Jam;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertJamsEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateJam;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateLatLon;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class JamTest extends AbstractBusinessEntityTest<Jam> {

    private static Random random = new Random();

    @Override
    public void testConstructor()  {
        Jam jam = generateJam();
        notNull(jam);
    }

    /**
     * Test Jam's toString method.
     */
    @Test
    public void testToString() {
        Jam jam = generateJam();
        assertNotEquals(jam.toString(), "");
    }

    @Test(expected=ValidationException.class)
    public void testNullPoints() {
        new Jam(null, 1.0f, 1);
    }

    @Test(expected = ValidationException.class)
    public void testEmptyPoints() {
        new Jam(new ArrayList<>(), 1.0f, 1);
    }


    @Test(expected=ValidationException.class)
    public void testNegSpeed() {
        new Jam(Arrays.asList(generateLatLon(), generateLatLon()), -1.0f, 1);
    }


    @Test(expected=ValidationException.class)
    public void testNegDelay() {
        new Jam(Arrays.asList(generateLatLon(), generateLatLon()), 1.0f, -1);
    }

    @Test
    public void testSetSpeed() {
        Jam jam = generateJam();
        float speed = Math.abs(random.nextFloat());
        jam.setSpeed(speed);
        assertEquals(jam.getSpeed(),speed,0.0);
    }

    @Test(expected = ValidationException.class)
    public void testSetNegSpeed() {
        generateJam().setSpeed(-1*Math.abs(random.nextFloat()));
    }

    @Test
    public void testSetDelay() {
        Jam jam = generateJam();
        int delay = Math.abs(random.nextInt());
        jam.setDelay(delay);
        assertEquals(jam.getDelay(),delay);
    }

    @Test(expected = ValidationException.class)
    public void testSetNegDelay() {
        generateJam().setDelay(-1*Math.abs(random.nextInt()));
    }

    @Test
    public void testDeepCopy() {
        Jam jam = generateJam();
        Jam deepCopy = jam.deepCopy();
        assertFalse(jam == deepCopy);
        assertJamsEqual(jam,deepCopy);
    }
}
