package be.ugent.vopro5.backend.businesslayer.applicationfacade.event;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.EVENTTYPE_INDEX_ENDPOINT;
import static org.junit.Assert.assertTrue;

/**
 * Created by anton on 3/5/16.
 */
public class EventTypeIndexControllerUnitTest extends ControllerTest {

    /**
     * Test GET'ting event types.
     *
     * @throws Exception
     */
    @Test
    public void getEventTypes() throws Exception{
        JsonNode node = performGetRequestNoContent(EVENTTYPE_INDEX_ENDPOINT);

        assertTrue(node.isArray());
    }
}
