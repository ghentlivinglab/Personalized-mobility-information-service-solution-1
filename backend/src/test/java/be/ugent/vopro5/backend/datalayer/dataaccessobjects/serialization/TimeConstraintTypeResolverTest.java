package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.TimeConstraint;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.TimeIntervalConstraint;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WeekDayConstraint;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTimeIntervalConstraint;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateWeekDayConstraint;
import static org.junit.Assert.assertEquals;

/**
 * Created by evert on 4/11/16.
 */
public class TimeConstraintTypeResolverTest extends TypeResolverTest {
    @Override
    public void setUp() throws Exception {
        typeResolver = new TimeConstraintTypeResolver();
    }

    @Override
    public void idFromValue() throws Exception {
        TimeConstraint timeIntervalConstraint = generateTimeIntervalConstraint();
        TimeConstraint weekDayConstraint = generateWeekDayConstraint();
        assertEquals("TIME_INTERVAL_CONSTRAINT", typeResolver.idFromValue(timeIntervalConstraint));
        assertEquals("WEEK_DAY_CONSTRAINT", typeResolver.idFromValue(weekDayConstraint));
    }

    @Override
    public void idFromValueAndType() throws Exception {
        assertEquals("TIME_INTERVAL_CONSTRAINT", typeResolver.idFromValueAndType(null, TimeIntervalConstraint.class));
        assertEquals("WEEK_DAY_CONSTRAINT", typeResolver.idFromValueAndType(null, WeekDayConstraint.class));
    }

    @Override
    public void typeFromId() throws Exception {
        assertEquals(TimeIntervalConstraint.class, typeResolver.typeFromId(null, "TIME_INTERVAL_CONSTRAINT").getRawClass());
        assertEquals(WeekDayConstraint.class, typeResolver.typeFromId(null, "WEEK_DAY_CONSTRAINT").getRawClass());
    }

}