package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.GenericEvent;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateGenericEvent;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateWazeEvent;
import static org.junit.Assert.assertEquals;

/**
 * Created by evert on 27/03/16.
 */
public class EventTypeResolverTest extends TypeResolverTest {

    @Override
    public void setUp() throws Exception {
        typeResolver = new EventTypeResolver();
    }

    @Override
    public void idFromValue() throws Exception {
        Event event = generateGenericEvent();
        Event wazeEvent = generateWazeEvent();
        assertEquals("GENERIC_EVENT", typeResolver.idFromValue(event));
        assertEquals("WAZE_EVENT", typeResolver.idFromValue(wazeEvent));
    }

    @Override
    public void idFromValueAndType() throws Exception {
        assertEquals("GENERIC_EVENT", typeResolver.idFromValueAndType(null, GenericEvent.class));
        assertEquals("WAZE_EVENT", typeResolver.idFromValueAndType(null, WazeEvent.class));
    }

    @Override
    public void typeFromId() throws Exception {
        assertEquals(GenericEvent.class, typeResolver.typeFromId(null, "GENERIC_EVENT").getRawClass());
        assertEquals(WazeEvent.class, typeResolver.typeFromId(null, "WAZE_EVENT").getRawClass());
    }
}