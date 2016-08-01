package be.ugent.vopro5.backend.businesslayer.applicationfacade.event;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.util.ObjectGeneration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.EVENT_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidEvent;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Michael Weyns on 3/03/2016.
 */
public class EventIndexControllerUnitTest extends ControllerTest {

    /**
     * Try GET'ting events relevant for a user which does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testGetEventsWithUserNotFound() throws Exception {
        String userId = UUID.randomUUID().toString();

        when(userDAO.find(userId)).thenReturn(null);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn(UUID.fromString(userId));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        performRequest(
                HttpMethod.GET,
                EVENT_INDEX_ENDPOINT,
                null,
                new HashMap<String, String>() {{
                    put("user_id", userId);
                }}
        ).andExpect(status().isNotFound());
    }

    /**
     * Test GET'ting events relevant for a user.
     *
     * @throws Exception
     */
    @Test
    public void testGetEventsWithUser() throws Exception {
        // TODO: when we implement the new way to link events to users
    }

    @Test
    public void testGetEvents() throws Exception {
        List<Event> events = new ArrayList<>();
        // Generate some events
        for (int i = 0; i < 10; i++) {
            Event event = ObjectGeneration.generateGenericEvent();
            event.setActive(i % 2 == 0);
            events.add(event);
        }
        when(eventDAO.listAll()).thenReturn(events);
        when(eventDAO.listAllActive()).thenReturn(events.stream().filter(Event::isActive).collect(Collectors.toList()));

        // When not specified, only active events should be returned
        MvcResult mvcResult = performRequest(
                HttpMethod.GET,
                EVENT_INDEX_ENDPOINT,
                null,
                null
        ).andExpect(status().isOk()).andReturn();
        JsonNode node = toJsonNode(mvcResult);
        assertTrue(node.isArray());
    }

    /**
     * Test creating an event successfully
     *
     * @throws Exception
     */
    @Test
    public void testCreateEvent() throws Exception {
        doNothing().when(eventDAO).insert(any());

        JsonNode event = ObjectGeneration.generateAPIEventJsonNode();
        MvcResult mvcResult = performRequest(
                HttpMethod.POST,
                EVENT_INDEX_ENDPOINT,
                event.toString(),
                null
        ).andExpect(status().isCreated()).andReturn();

        JsonNode node = toJsonNode(mvcResult);
        assertIsValidEvent(node);

        verify(eventDAO, times(1)).insert(any());
    }

    /**
     * Test creating an event with an invalid event type
     *
     * @throws Exception
     */
    @Test
    public void testCreateEventWrongEventType() throws Exception {
        ObjectNode event = ObjectGeneration.generateAPIEventJsonNode();
        ObjectNode eventType = ObjectGeneration.generateAPIEventTypeJsonNode();
        eventType.put("type", "some_non_existing_type");
        event.set("type", eventType);

        performRequest(
                HttpMethod.POST,
                EVENT_INDEX_ENDPOINT,
                event.toString(),
                null
        ).andExpect(status().isBadRequest());
    }

}
