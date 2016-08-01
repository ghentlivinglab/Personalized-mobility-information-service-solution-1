package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getTravelEntityEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidTravel;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by maarten on 04.03.16.
 */
public class TravelEntityControllerUnitTest extends ControllerTest {

    /**
     * GET's a travel successfully and checks the response
     *
     * @throws Exception
     */
    @Test
    public void testGetTravel() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        user.addTravel(travel);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        JsonNode node = performGetRequestNoContent(
                getTravelEntityEndpoint(user.getIdentifier().toString(), travel.getIdentifier().toString())
        );
        assertIsValidTravel(node);
    }

    /**
     * Tries to GET a travel for a user that does not exist.
     * Should result in 404 error.
     *
     * @throws Exception
     */
    @Test
    public void testGetTravelUserDoesNotExist() throws Exception {
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);

        performGetRequestNotFound(getTravelEntityEndpoint(IMAGINARY_USER, IMAGINARY_TRAVEL));
    }

    /**
     * Tries to GET a travel which does not exist.
     * Should result in 404 error.
     *
     * @throws Exception
     */
    @Test
    public void testGetTravelDoesNotExist() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(IMAGINARY_TRAVEL)).thenReturn(null);

        performGetRequestNotFound(
                getTravelEntityEndpoint(user.getIdentifier().toString(), IMAGINARY_TRAVEL)
        );
    }

    /**
     * Tries to update a travel on a user which does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateTravelUserNotFound() throws Exception {
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        performRequest(
                HttpMethod.PUT,
                getTravelEntityEndpoint(IMAGINARY_USER, IMAGINARY_TRAVEL),
                generateAPITravelJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    /**
     * Tries to update a travel on a travel which does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateTravelNotFound() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(IMAGINARY_TRAVEL)).thenReturn(null);

        performRequest(
                HttpMethod.PUT,
                getTravelEntityEndpoint(user.getIdentifier().toString(), IMAGINARY_TRAVEL),
                generateAPITravelJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    /**
     * Update a travel successfully.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateTravel() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        user.addTravel(travel);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        performRequest(
                HttpMethod.PUT,
                getTravelEntityEndpoint(user.getIdentifier().toString(), travel.getIdentifier().toString()),
                generateAPITravelJsonNode().toString(),
                null).andExpect(status().isOk());

        verify(travelDAO, times(1)).update(any());
    }

    /**
     * Delete a travel successfully.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteTravel() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        user.addTravel(travel);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);
        doNothing().when(userDAO).update(any());

        performDeleteNoContent(
                getTravelEntityEndpoint(user.getIdentifier().toString(), travel.getIdentifier().toString())
        );

        verify(travelDAO, times(1)).delete(any());
        verify(userDAO, times(1)).update(any());
    }

    @Test
    public void testDeleteTravelNotFound() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(IMAGINARY_TRAVEL)).thenReturn(null);

        performDeleteNotFound(getTravelEntityEndpoint(user.getIdentifier().toString(), IMAGINARY_TRAVEL));

        verify(travelDAO, times(0)).delete(any());
    }

    @Test
    public void testDeleteTravelUserNotFound() throws Exception {
        Travel travel = generateTravel();

        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);

        performDeleteNotFound(
                getTravelEntityEndpoint(IMAGINARY_USER, travel.getIdentifier().toString())
        );

        verify(travelDAO, times(0)).delete(any());
    }
}
