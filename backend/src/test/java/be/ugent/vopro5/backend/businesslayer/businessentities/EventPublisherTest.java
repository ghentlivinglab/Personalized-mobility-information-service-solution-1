package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventPublisher;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by evert on 4/12/16.
 */
public class EventPublisherTest {

    private Random random = new Random();

    @Test
    public void testFactory() {
        EventPublisher waze = EventPublisher.factory(EventPublisher.EventPublisherType.WAZE, null, null);
        assertEquals(waze.getType(), EventPublisher.EventPublisherType.WAZE);

        EventPublisher external = EventPublisher.factory(EventPublisher.EventPublisherType.EXTERNAL, random.nextInt() + "", random.nextInt() + "");
        assertEquals(external.getType(), EventPublisher.EventPublisherType.EXTERNAL);
    }

    @Test(expected = ValidationException.class)
    public void testFactoryTypeNull() {
        EventPublisher.factory(null, null, null);
    }
}
