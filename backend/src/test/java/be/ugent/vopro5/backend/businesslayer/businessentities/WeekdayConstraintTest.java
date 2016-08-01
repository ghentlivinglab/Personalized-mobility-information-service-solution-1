package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.WeekDayConstraint;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTravel;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateWeekDayConstraint;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by lukas on 21/03/2016.
 */
public class WeekdayConstraintTest extends AbstractImmutableEntityTest<WeekDayConstraint> {

    private static final Random random = new Random();

    @Test
    @Override
    public void testConstructor() {
        WeekDayConstraint constraint = generateWeekDayConstraint();
        notNull(constraint);
    }

    /**
     * Test construction with too many recurring values.
     */
    @Test(expected=ValidationException.class)
    public void testTooManyValues() {
        new WeekDayConstraint(new boolean[]{true, true, false, false, true, true, false, false});
    }

    /**
     * Test construction with no recurring values.
     */
    @Test(expected=ValidationException.class)
    public void testNoArray() {
        new WeekDayConstraint(null);
    }

    /**
     * Test WeekDayConstraint's getRecurring method.
     */
    @Test
    public void testGetRecurring() {
        boolean[] recurring = new boolean[] {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
        random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        WeekDayConstraint constraint = new WeekDayConstraint(recurring);

        assertEquals(recurring, constraint.getRecurring());
    }

    @Test
    public void testValid() {
        LocalDateTime localDateTime = LocalDateTime.now();
        WeekDayConstraint constraint = generateWeekDayConstraint();
        constraint.getRecurring()[localDateTime.getDayOfWeek().getValue() - 1] = true;

        assertTrue(constraint.valid(localDateTime));
    }

    @Test
    @Override
    public void testHashCodeUniqueness() {
        WeekDayConstraint constraint1 = generateWeekDayConstraint();
        WeekDayConstraint constraint2 = generateWeekDayConstraint();
        assertNotEquals(constraint1.hashCode(), constraint2.hashCode());
    }

    @Override
    public void testNonEquality(WeekDayConstraint w1, WeekDayConstraint w2) {
        assertNotEquals(w1, w2);
    }

    @Test
    @Override
    public void testEquals() {
        WeekDayConstraint constraint1 = generateWeekDayConstraint();
        assertEquals(constraint1, constraint1);

        boolean[] recurring = new boolean[] {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
                random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        int index = random.nextInt(recurring.length - 1);
        recurring[index] = !constraint1.getRecurring()[index];

        assertFalse(constraint1.equals(generateTravel()));
        assertFalse(constraint1.equals(null));
        WeekDayConstraint constraint2 = new WeekDayConstraint(recurring);
        testNonEquality(constraint1, constraint2);

        notNullNotBlankString(constraint1);
    }

    /**
     * Test WeekDayConstraint's toString method.
     */
    @Test
    public void testToString() {
        WeekDayConstraint constraint = generateWeekDayConstraint();
        assertNotNull(constraint.toString());
    }

}
