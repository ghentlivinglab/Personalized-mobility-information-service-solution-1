package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getTravelIndexEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidTravel;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPITravelJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 04.03.16.
 */
public class TravelIndexControllerUnitTest extends ControllerTest {

    /**
     * Test creation of a travel when user exists and input is valid.
     * Should result in 201.
     *
     * @throws Exception
     */
    @Test
    public void testCreateTravel() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        doNothing().when(userDAO).update(any());

        MvcResult mvcResult = performRequest(
                HttpMethod.POST,
                getTravelIndexEndpoint(user.getIdentifier().toString()),
                generateAPITravelJsonNode().toString(),
                null).andExpect(status().isCreated()).andReturn();

        JsonNode travel = toJsonNode(mvcResult);
        assertIsValidTravel(travel);

        verify(userDAO, times(1)).update(any());
    }

    /**
     * Test creation of a travel when user does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testCreateTravelUserNotFound() throws Exception {
        when(userDAO.find(anyString())).thenReturn(null);
        performRequest(
                HttpMethod.POST,
                getTravelIndexEndpoint(UUID.randomUUID().toString()),
                generateAPITravelJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    /**
     * Test getting travels of a user that does not exist
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testGetTravelsUserNotFound() throws Exception {
        when(userDAO.find(anyString())).thenReturn(null);
        performRequest(
                HttpMethod.GET,
                getTravelIndexEndpoint(UUID.randomUUID().toString()),
                null,
                null).andExpect(status().isNotFound());
    }

    /**
     *
     * Test the method getTravels in both successful and unsuccessful scenarios.
     * In successful case check if the result has the correct format and size.
     *
     * @throws Exception
     */
    @Test
    public void testGetTravels() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        MvcResult mvcResult = performRequest(
                HttpMethod.GET,
                getTravelIndexEndpoint(user.getIdentifier().toString()),
                null,
                null).andExpect(status().isOk()).andReturn();

        JsonNode node = toJsonNode(mvcResult);
        assertTrue(node.isArray());
        assertEquals(user.getTravels().size(), node.size());
    }

}
