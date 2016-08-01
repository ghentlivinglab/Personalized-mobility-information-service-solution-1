package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventPublisher;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEventPublisher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by evert on 26/03/16.
 */
public class EventPublisherTypeResolverTest extends TypeResolverTest {

    @Override
    public void setUp() throws Exception {
        typeResolver = new EventPublisherTypeResolver();
    }

    @Test
    public void idFromValue() throws Exception {
        assertEquals("WAZE_EVENT_PUBLISHER", typeResolver.idFromValue(new WazeEventPublisher()));
        assertEquals("EXTERNAL_EVENT_PUBLISHER", typeResolver.idFromValue(new EventPublisher.ExternalEventPublisher("name", "url")));
    }

    @Test
    public void idFromValueAndType() throws Exception {
        assertEquals("WAZE_EVENT_PUBLISHER", typeResolver.idFromValueAndType(null, WazeEventPublisher.class));
        assertEquals("EXTERNAL_EVENT_PUBLISHER", typeResolver.idFromValueAndType(null, EventPublisher.ExternalEventPublisher.class));
    }

    @Test
    public void typeFromId() throws Exception {
        assertEquals(WazeEventPublisher.class, typeResolver.typeFromId(null, "WAZE_EVENT_PUBLISHER").getRawClass());
        assertEquals(EventPublisher.ExternalEventPublisher.class, typeResolver.typeFromId(null, "EXTERNAL_EVENT_PUBLISHER").getRawClass());
    }
}