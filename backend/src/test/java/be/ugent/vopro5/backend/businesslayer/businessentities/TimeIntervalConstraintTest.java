package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.TimeIntervalConstraint;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTimeIntervalConstraint;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTravel;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class TimeIntervalConstraintTest extends AbstractImmutableEntityTest<TimeIntervalConstraint> {


    private static final int SECONDS_IN_DAY = 60 * 60 * 24;
    private static final Random random = new Random();

    @Test
    @Override
    public void testConstructor() {
        TimeIntervalConstraint constraint = generateTimeIntervalConstraint();
        notNull(constraint);
    }

    /**
     * Test TimeIntervalConstraint's getStarttime method.
     */
    @Test
    public void testGetStarttime() {
        LocalTime starttime = LocalTime.now();
        TimeIntervalConstraint constraint = new TimeIntervalConstraint(starttime,
                Duration.ofSeconds(random.nextInt(SECONDS_IN_DAY)));

        assertEquals(starttime, constraint.getStarttime());
    }

    /**
     * Test TimeIntervalConstraint's getDuration method.
     */
    @Test
    public void testGetDuration() {
        Duration duration = Duration.ofSeconds(random.nextInt(SECONDS_IN_DAY));
        TimeIntervalConstraint constraint = new TimeIntervalConstraint(LocalTime.now(), duration);

        assertEquals(duration, constraint.getDuration());
    }

    /**
     * Test TimeIntervalConstraint's getEndtime method.
     */
    @Test
    public void testGetEndtime() {
        TimeIntervalConstraint constraint = generateTimeIntervalConstraint();

        assertEquals(constraint.getStarttime().plus(constraint.getDuration()), constraint.getEndtime());
    }

    /**
     * Test TimeIntervalConstraint's valid method.
     */
    @Test
    public void testValid() {
        TimeIntervalConstraint constraint1 = new TimeIntervalConstraint(LocalTime.NOON, Duration.of(1, ChronoUnit.HALF_DAYS));
        TimeIntervalConstraint constraint2 = new TimeIntervalConstraint(LocalTime.NOON, Duration.of(2, ChronoUnit.HALF_DAYS));

        long duration1 = (Math.abs(random.nextLong()) + 1) % constraint1.getDuration().toMinutes() - 1;
        LocalTime valid1 = constraint1.getStarttime().plusMinutes(duration1);

        long duration2 = constraint1.getDuration().plus(1, ChronoUnit.HOURS).toMinutes();
        LocalTime invalid = constraint1.getStarttime().plusMinutes(duration2);

        long duration3 = Duration.of(1, ChronoUnit.HALF_DAYS).toMinutes() +
                (Math.abs(new Random().nextLong()) + 1) % Duration.of(1, ChronoUnit.HALF_DAYS).toMinutes() - 1;
        LocalTime valid2 = constraint2.getStarttime().plusMinutes(duration3);

        assertTrue(constraint1.valid(valid1.atDate(LocalDate.now())));
        assertFalse(constraint1.valid(invalid.atDate(LocalDate.now())));
        assertTrue(constraint2.valid(valid2.atDate(LocalDate.now())));
    }

    @Test(expected=ValidationException.class)
    public void testEmptyStartTime() {
        new TimeIntervalConstraint(null, java.time.Duration.ofSeconds(random.nextInt(SECONDS_IN_DAY)));
    }

    @Test(expected=ValidationException.class)
    public void testEmptyDuration() {
        new TimeIntervalConstraint(LocalTime.ofSecondOfDay(random.nextInt(SECONDS_IN_DAY)).withSecond(0),null);
    }

    @Test
    @Override
    public void testHashCodeUniqueness() {
        TimeIntervalConstraint constraint1 = generateTimeIntervalConstraint();
        TimeIntervalConstraint constraint2 = generateTimeIntervalConstraint();
        assertNotEquals(constraint1.hashCode(), constraint2.hashCode());
    }

    @Test
    @Override
    public void testEquals() {
        TimeIntervalConstraint constraint1 = generateTimeIntervalConstraint();
        assertEquals(constraint1, constraint1);
        int seconds = random.nextInt(SECONDS_IN_DAY);
        TimeIntervalConstraint constraint2 = new TimeIntervalConstraint(constraint1.getStarttime().plusSeconds(seconds), constraint1.getDuration().plusSeconds(seconds));
        testNonEquality(constraint1, constraint2);
        assertFalse(constraint1.equals(generateTravel()));
        assertFalse(constraint1.equals(null));
        notNullNotBlankString(constraint1);
    }

    @Override
    protected void testNonEquality(TimeIntervalConstraint constraint1, TimeIntervalConstraint constraint2) {
        threeWayNotEquals(constraint1, constraint2);

        constraint2 = new TimeIntervalConstraint(constraint1.getStarttime(),constraint2.getDuration());
        threeWayNotEquals(constraint1, constraint2);

        constraint2 = new TimeIntervalConstraint(constraint1.getStarttime(),constraint1.getDuration());
        twoWayEquals(constraint1, constraint2);
    }

    /**
     * Test TimeIntervalConstraint's toString method.
     */
    @Test
    public void testToString() {
        TimeIntervalConstraint constraint = generateTimeIntervalConstraint();
        assertNotNull(constraint.toString());
    }
}
