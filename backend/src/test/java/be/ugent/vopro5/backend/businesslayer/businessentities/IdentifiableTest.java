package be.ugent.vopro5.backend.businesslayer.businessentities;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Identifiable;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import org.junit.Test;

import java.util.*;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;

/**
 * Created by maarten on 28.03.16.
 */
public class IdentifiableTest {

    private static final Random random = new Random();

    @Test
    public void testEquals() {
        Identifiable travel = generateTravel();
        Identifiable travel1  = generateTravel();
        Identifiable pointOfInterest = generatePointOfInterest();


        assertFalse(travel.equals(travel1));
        assertFalse(travel.equals(null));
        assertFalse(travel.equals(pointOfInterest));
        assertTrue(travel.equals(travel));
    }

    @Test
    public void testGetIdentifier() {
        UUID uuid = UUID.randomUUID();
        Identifiable travel = new Travel(uuid,
                        random.nextInt() + "",
                        true,
                        generateAddress(),
                        generateAddress(),
                        new HashSet<Route>(),
                        new HashSet<>(Collections.singletonList(generateTimeIntervalConstraint()))
                );
        assertEquals(uuid, travel.getIdentifier());
    }

}
