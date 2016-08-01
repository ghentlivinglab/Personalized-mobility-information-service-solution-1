package be.ugent.vopro5.backend.businesslayer.applicationfacade.pointOfInterest;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getPOIEntityEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidPOI;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by anton on 3/26/16.
 */
public class PointOfInterestEntityControllerUnitTest extends ControllerTest {
    private static final String IMAGINARY_USER = "imaginary_user";
    private static final String IMAGINARY_POI = "imaginary_poi";


    /**
     * GET's a pointOfInterest successfully and checks the response
     *
     * @throws Exception
     */
    @Test
    public void testGetPointOfInterest() throws Exception {
        User user = generateUser();
        PointOfInterest poi = generatePointOfInterest();
        user.addPointOfInterest(poi);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        JsonNode node = performGetRequestNoContent(
                getPOIEntityEndpoint(user.getIdentifier().toString(), poi.getIdentifier().toString())
        );
        assertIsValidPOI(node);
    }

    /**
     * Tries to GET a pointOfInterest for a user that does not exist.
     * Should result in 404 error.
     *
     * @throws Exception
     */
    @Test
    public void testGetPOIUserDoesNotExist() throws Exception {
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        performGetRequestNotFound(getPOIEntityEndpoint(IMAGINARY_USER, IMAGINARY_POI));
    }

    /**
     * Tries to GET a pointOfInterest which does not exist.
     * Should result in 404 error.
     *
     * @throws Exception
     */
    @Test
    public void testGetPOIDoesNotExist() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(poiDAO.find(IMAGINARY_POI)).thenReturn(null);

        performGetRequestNotFound(getPOIEntityEndpoint(user.getIdentifier().toString(), IMAGINARY_POI));
    }

    /**
     * Tries to update a pointOfInterest on a user which does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testUpdatePOIUserNotFound() throws Exception {
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        performRequest(
                HttpMethod.PUT,
                getPOIEntityEndpoint(IMAGINARY_USER, IMAGINARY_POI),
                generateAPIPOIJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    /**
     * Tries to update a pointOfInterest on a pointOfInterest which does not exist.
     * Should result in a 404.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateTravelNotFound() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(poiDAO.find(IMAGINARY_POI)).thenReturn(null);

        performRequest(
                HttpMethod.PUT,
                getPOIEntityEndpoint(user.getIdentifier().toString(), IMAGINARY_POI),
                generateAPIPOIJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    /**
     * Update a pointOfInterest successfully.
     *
     * @throws Exception
     */
    @Test
    public void testUpdatePOI() throws Exception {
        User user = generateUser();
        PointOfInterest poi = generatePointOfInterest();
        user.addPointOfInterest(poi);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        performRequest(
                HttpMethod.PUT,
                getPOIEntityEndpoint(user.getIdentifier().toString(), poi.getIdentifier().toString()),
                generateAPIPOIJsonNode().toString(),
                null).andExpect(status().isOk());

        verify(poiDAO, times(1)).update(any());
    }

    /**
     * Delete a pointOfInterest successfully.
     *
     * @throws Exception
     */
    @Test
    public void testDeletePOI() throws Exception {
        User user = generateUser();
        PointOfInterest poi = generatePointOfInterest();
        user.addPointOfInterest(poi);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        doNothing().when(userDAO).update(any());

        performDeleteNoContent(getPOIEntityEndpoint(user.getIdentifier().toString(), poi.getIdentifier().toString()));

        verify(poiDAO, times(1)).delete(any());
        verify(userDAO, times(1)).update(any());
    }

    @Test
    public void testDeletePOINotFound() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(poiDAO.find(IMAGINARY_POI)).thenReturn(null);

        performDeleteNotFound(
                getPOIEntityEndpoint(user.getIdentifier().toString(), IMAGINARY_POI)
        );

        verify(poiDAO, times(0)).delete(any());
    }

    @Test
    public void testDeletePOIUserNotFound() throws Exception {
        PointOfInterest poi = generatePointOfInterest();

        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        when(poiDAO.find(poi.getIdentifier().toString())).thenReturn(poi);

        performDeleteNotFound(
                getPOIEntityEndpoint(IMAGINARY_USER, poi.getIdentifier().toString())
        );

        verify(poiDAO, times(0)).delete(any());
    }
}
