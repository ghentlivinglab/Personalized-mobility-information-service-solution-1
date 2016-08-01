package be.ugent.vopro5.backend.businesslayer.applicationfacade.event;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.util.ObjectGeneration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getEventEntityEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidError;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidEvent;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Michael Weyns on 3/03/2016.
 */
public class EventEntityControllerUnitTest extends ControllerTest {
    protected static final String IMAGINARY_EVENT = "imaginary_event";

    /**
     * Test retrieve an event by ID
     *
     * @throws Exception
     */
    @Test
    public void testGetEvent() throws Exception {
        Event event = ObjectGeneration.generateGenericEvent();
        when(eventDAO.find(event.getIdentifier().toString())).thenReturn(event);

        JsonNode node = performGetRequestNoContent(
                getEventEntityEndpoint(event.getIdentifier().toString())
        );
        assertIsValidEvent(node);
    }

    /**
     * Test retrieve an event which does not exist
     *
     * @throws Exception
     */
    @Test
    public void testGetEventNotFound() throws Exception {
        when(eventDAO.find(IMAGINARY_EVENT)).thenReturn(null);

        performGetRequestNotFound(getEventEntityEndpoint(IMAGINARY_EVENT));
    }

    /**
     * Test update an event which does not exist
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEventNotFound() throws Exception {
        when(eventDAO.find(IMAGINARY_EVENT)).thenReturn(null);

        MvcResult mvcResult = performRequest(
                HttpMethod.PUT,
                getEventEntityEndpoint(IMAGINARY_EVENT),
                ObjectGeneration.generateAPIEventJsonNode().toString(),
                null
        ).andReturn();
        JsonNode node = toJsonNode(mvcResult);
        assertIsValidError(node);
    }

    /**
     * Test update an event which is already marked as non active
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEventAlreadyNonActive() throws Exception {
        Event event = ObjectGeneration.generateGenericEvent();
        event.setActive(false);

        when(eventDAO.find(event.getIdentifier().toString())).thenReturn(event);

        performRequest(
                HttpMethod.PUT,
                getEventEntityEndpoint(event.getIdentifier().toString()),
                ObjectGeneration.generateAPIEventJsonNode().toString(),
                null
        ).andExpect(status().isConflict());
    }

    /**
     * The response of an event includes a publication time and a last edit time.
     * This test tries to set these values in the input.
     * The backend should ignore those inputs.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEventFieldsNotAllowed() throws Exception {
        Event event = ObjectGeneration.generateGenericEvent();
        when(eventDAO.find(event.getIdentifier().toString())).thenReturn(event);

        ObjectNode eventJson = ObjectGeneration.generateAPIEventJsonNode();

        // The following attributes should be ignored when updating the event
        eventJson.put("publication_time", LocalDateTime.of(1990, 1, 1, 10, 0, 0).toString());
        eventJson.put("last_edit_time", LocalDateTime.of(3000, 1, 1, 10, 0, 0).toString());

        MvcResult mvcResult = performRequest(
                HttpMethod.PUT,
                getEventEntityEndpoint(event.getIdentifier().toString()),
                eventJson.toString(),
                null
        ).andReturn();
        JsonNode returnedEvent = toJsonNode(mvcResult);

        assertNotEquals(eventJson.get("publication_time").asText(), returnedEvent.get("publication_time").asText());
        assertNotEquals(eventJson.get("last_edit_time").asText(), returnedEvent.get("last_edit_time").asText());
    }
}
