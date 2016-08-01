package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertSetOfRoutesEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by thibault on 3/1/16.
 */
public class TravelTest extends AbstractBusinessEntityTest<Travel> {

    private static Random random = new Random();

    @Override
    public void testConstructor()  {
        Travel travel = generateTravel();
        notNull(travel);
    }

    /**
     * Test Travel's alternative constructor.
     */
    @Test
    public void testConstructorWithoutUUID() {
        boolean[] recurring = new boolean[] {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
        random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        Travel travel = new Travel(random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                generateTimeIntervalConstraint(),
                recurring);
        notNull(travel);
    }

    /**
     * Test construction without an id.
     */
    @Test(expected=ValidationException.class)
    public void testNoId() {
        new Travel(null,
                random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                new HashSet<Route>(),
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint())));
    }

    @Test(expected=ValidationException.class)
    public void testBlankName()  {
        new Travel(UUID.randomUUID(),
                "",
                true,
                generateAddress(),
                generateAddress(),
                new HashSet<Route>(),
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint())));
    }

    @Test(expected=ValidationException.class)
    public void testNullStartPoint()  {
        new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                null,
                generateAddress(),
                new HashSet<Route>(),
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint())));
    }

    @Test(expected=ValidationException.class)
    public void testNullEndPoint()  {
        new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                generateAddress(),
                null,
                new HashSet<Route>(),
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint())));
    }

    @Test(expected=ValidationException.class)
    public void testNullRoutes()  {
        new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                null,
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint())));
    }

    @Test(expected=ValidationException.class)
    public void testEmptyIntervalConstraint()  {
        new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                new HashSet<Route>(),
                new HashSet<>());
    }

    /**
     * Test Travel's toString method.
     */
    @Test
    public void testToString() throws ValidationException{
        Travel travel = generateTravel();
        assertNotNull(travel.toString());
    }

    @Test(expected = ValidationException.class)
    public void testSetBlankName() {
        Travel travel = generateTravel();
        travel.setName("");
    }

    @Test
    public void testSetName() {
        Travel travel = generateTravel();
        String name = random.nextInt() + "";
        travel.setName(name);
        assertEquals(name,travel.getName());
    }

    @Test
    public void testIsArrivalTime() {
        Travel travel = generateTravel();
        boolean isArrivalTime = !travel.isArrivalTime();
        travel.setIsArrivalTime(isArrivalTime);
        assertTrue(travel.isArrivalTime() == isArrivalTime);
    }

    @Test
    public void testGetStartAndEndPoint() {
        Address startPoint = generateAddress();
        Address endPoint = generateAddress();
        Travel travel = new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                startPoint,
                endPoint,
                new HashSet<Route>(),
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint()))
        );
        assertTrue(startPoint.equals(travel.getStartPoint()));
        assertTrue(endPoint.equals(travel.getEndPoint()));
    }

    @Test
    public void testGetRoutes() {
        int numberOfRoutes = random.nextInt(4) + 1;
        Set<Route> routeSet = new HashSet<>();
        for(int i=0;i<numberOfRoutes;i++) {
            routeSet.add(generateRoute());
        }
        Travel travel = new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                routeSet,
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint()))
        );
        assertSetOfRoutesEqual(routeSet,travel.getRoutes());
    }

    @Test
    public void testAddAndRemoveRoute() {
        Travel travel = generateTravel();
        Route route = travel.getRoutes().iterator().next();
        int prevSize = travel.getRoutes().size();
        travel.removeRoute(route);
        assertFalse(travel.getRoutes().contains(route));
        assertTrue(travel.getRoutes().size() == prevSize - 1);

        travel.addRoute(route);
        assertTrue(travel.getRoutes().contains(route));
        assertTrue(travel.getRoutes().size() == prevSize);
    }

    @Test(expected = ValidationException.class)
    public void testAddNullRoute() {
        generateTravel().addRoute(null);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveNotContainedRoute() {
        Travel travel = generateTravel();
        Route route = travel.getRoutes().iterator().next();
        travel.removeRoute(route);
        travel.removeRoute(route);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveFromEmptyRoutes() {
        new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                new HashSet<Route>(),
                new HashSet<>(Arrays.asList(generateTimeIntervalConstraint()))
        ).removeRoute(generateRoute());
    }

    @Test
    public void testGetTimeConstraints() {
        Set<TimeConstraint> timeConstraintSet = new HashSet<>();
        timeConstraintSet.add(generateTimeIntervalConstraint());
        timeConstraintSet.add(generateWeekDayConstraint());
        Travel travel = new Travel(UUID.randomUUID(),
                random.nextInt() + "",
                true,
                generateAddress(),
                generateAddress(),
                new HashSet<>(),
                timeConstraintSet
        );
        assertEquals(timeConstraintSet,travel.getTimeConstraints());
    }

    @Test
    public void testAddAndRemoveTimeConstraint() {
        Travel travel = generateTravel();
        TimeConstraint timeConstraint = travel.getTimeConstraints().iterator().next();
        int prevSize = travel.getTimeConstraints().size();
        travel.removeTimeContstraint(timeConstraint);
        assertFalse(travel.getTimeConstraints().contains(timeConstraint));
        assertEquals(travel.getTimeConstraints().size(),prevSize - 1);

        travel.addTimeConstraint(timeConstraint);
        assertTrue(travel.getTimeConstraints().contains(timeConstraint));
        assertEquals(travel.getTimeConstraints().size(),prevSize);
    }

    @Test(expected = ValidationException.class)
    public void testAddNullTimeConstraint() {
        generateTravel().addTimeConstraint(null);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveNotContainedTimeConstraint() {
        Travel travel = generateTravel();
        TimeConstraint timeConstraint = generateTimeIntervalConstraint();
        travel.removeTimeContstraint(timeConstraint);
    }

    @Test(expected = ValidationException.class)
    public void testRemoveLastTimeConstraint() {
        Travel travel = generateTravel();
        TimeConstraint timeConstraint = travel.getTimeConstraints().iterator().next();
        travel.removeTimeContstraint(timeConstraint);
        travel.removeTimeContstraint(travel.getTimeConstraints().iterator().next());
    }

    @Test
    public void testTransferProperties() {
        Travel travel = generateTravel();
        Travel travel2 = generateTravel();
        travel.transferProperties(travel2);
        assertTrue(travel.isArrivalTime() == travel2.isArrivalTime());
        assertTrue(travel.getName().equals(travel2.getName()));
        assertEquals(travel.getTimeConstraints(),travel2.getTimeConstraints());
    }
}
