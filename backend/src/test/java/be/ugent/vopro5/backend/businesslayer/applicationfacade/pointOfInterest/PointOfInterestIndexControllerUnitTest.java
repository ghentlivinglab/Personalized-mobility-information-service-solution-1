package be.ugent.vopro5.backend.businesslayer.applicationfacade.pointOfInterest;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getPOIIndexEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidPOI;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIPOIJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by anton on 3/26/16.
 */
public class PointOfInterestIndexControllerUnitTest extends ControllerTest {

    /**
     * Test creation of a pointOfInterest when user exists and input is valid.
     * Should result in 201.
     *
     * @throws Exception
     */
    @Test
    public void testCreatePOI() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        doNothing().when(userDAO).update(any());

        MvcResult mvcResult = performRequest(
                HttpMethod.POST,
                getPOIIndexEndpoint(user.getIdentifier().toString()),
                generateAPIPOIJsonNode().toString(),
                null).andExpect(status().isCreated()).andReturn();

        JsonNode poi = toJsonNode(mvcResult);
        assertIsValidPOI(poi);

        verify(userDAO, times(1)).update(any());
    }

    /**
     * Test creation of a pointOfInterest when user does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testCreatePOIUserNotFound() throws Exception {
        when(userDAO.find(anyString())).thenReturn(null);
        performRequest(
                HttpMethod.POST,
                getPOIIndexEndpoint(UUID.randomUUID().toString()),
                generateAPIPOIJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    /**
     * Test getting pois of a user that does not exist
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testGetPOIsUserNotFound() throws Exception {
        when(userDAO.find(anyString())).thenReturn(null);
        performRequest(
                HttpMethod.GET,
                getPOIIndexEndpoint(UUID.randomUUID().toString()),
                null,
                null).andExpect(status().isNotFound());
    }

    /**
     *
     * Test the method getPOIs in both successful and unsuccessful scenarios.
     * In successful case check if the result has the correct format and size.
     *
     * @throws Exception
     */
    @Test
    public void testGetPOIs() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        MvcResult mvcResult = performRequest(
                HttpMethod.GET,
                getPOIIndexEndpoint(user.getIdentifier().toString()),
                null,
                null).andExpect(status().isOk()).andReturn();

        JsonNode node = toJsonNode(mvcResult);
        assertTrue(node.isArray());
        assertEquals(user.getPointsOfInterest().size(), node.size());
    }
}
