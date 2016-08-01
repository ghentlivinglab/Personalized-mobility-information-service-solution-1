package be.ugent.vopro5.backend.integration;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.*;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertGenericEventsJsonNodeEquals;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedGenericEventIsValid;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIEventJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIOperatorJsonNode;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 26.04.16.
 */
public class EventIntegrationTest extends IntegrationTest {

    @Test
    public void createEventTest() throws Exception {
        AccessDetails operatorAccessDetails = createPersonAndGetAccessDetails(generateAPIOperatorJsonNode(),OPERATOR_INDEX_ENDPOINT,true);
        MvcResult createdEvent = performRequest(HttpMethod.POST, EVENT_INDEX_ENDPOINT, generateAPIEventJsonNode().toString(), null, operatorAccessDetails.getAccessToken())
                .andExpect(status().isCreated()).andReturn();

        MvcResult getEvent = performRequest(HttpMethod.GET, getEventEntityEndpoint(toJsonNode(createdEvent).get("id").asText()), null, null, operatorAccessDetails.getAccessToken())
                .andExpect(status().isOk()).andReturn();

        assertGenericEventsJsonNodeEquals(toJsonNode(createdEvent),toJsonNode(getEvent));
    }

    @Test
    public void viewAllEventsTest() throws Exception {
        int numEvents = random.nextInt(15) + 5;
        AccessDetails operatorAccessDetails = createPersonAndGetAccessDetails(generateAPIOperatorJsonNode(),OPERATOR_INDEX_ENDPOINT,true);
        for (int i = 0; i < numEvents; i++) {
            performRequest(HttpMethod.POST, EVENT_INDEX_ENDPOINT, generateAPIEventJsonNode().toString(), null, operatorAccessDetails.getAccessToken())
                    .andExpect(status().isCreated());
        }

        MvcResult result = performRequest(HttpMethod.GET, EVENT_INDEX_ENDPOINT, null, null, operatorAccessDetails.getAccessToken())
                .andExpect(status().isOk()).andReturn();
        assertEquals(numEvents, toJsonNode(result).size());
    }

    @Test
    public void updateEventTest() throws Exception {
        AccessDetails operatorAccessDetails = createPersonAndGetAccessDetails(generateAPIOperatorJsonNode(),OPERATOR_INDEX_ENDPOINT,true);
        MvcResult createdEvent = performRequest(HttpMethod.POST, EVENT_INDEX_ENDPOINT, generateAPIEventJsonNode().toString(), null, operatorAccessDetails.getAccessToken())
                .andExpect(status().isCreated()).andReturn();

        ObjectNode eventNode = generateAPIEventJsonNode();

        JsonNode updatedEvent = toJsonNode(
                performRequest(
                        HttpMethod.PUT,
                        getEventEntityEndpoint(toJsonNode(createdEvent).get("id").asText()),
                        eventNode.toString(),
                        null,
                        operatorAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn());

        //apparently we need to convert an ObjectNode explicitly as follows.
        assertUpdatedGenericEventIsValid(updatedEvent,mapper.readTree(eventNode.toString()));

        //DOES NOT WORK with implicit cast
        //assertUpdatedGenericEventIsValid(updatedEvent,eventNode);
    }

}
