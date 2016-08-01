package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel.route;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getRouteIndexEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidRoute;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIRouteJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by anton on 3/27/16.
 */
public class RouteIndexControllerUnitTest extends ControllerTest {

    @Test
    public void getRoutes() throws Exception {
        User user = generateUser();
        when(userDAO.find(eq(user.getIdentifier().toString()))).thenReturn(user);

        Travel travel = user.getTravels().stream().findAny().orElse(null);

        MvcResult result = performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(user.getIdentifier().toString(), travel.getIdentifier().toString()),
                null,
                null).andExpect(status().isOk()).andReturn();

        JsonNode node = toJsonNode(result);
        assertEquals(travel.getRoutes().size(), node.size());
    }

    @Test
    public void getRoutesNoUser() throws Exception {
        when(userDAO.find(any())).thenReturn(null);

        performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                null,
                null).andExpect(status().isNotFound());
    }

    @Test
    public void getRoutesNoTravel() throws Exception {
        User user = generateUser();
        when(userDAO.find(eq(user.getIdentifier().toString()))).thenReturn(user);

        performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(user.getIdentifier().toString(), UUID.randomUUID().toString()),
                null,
                null).andExpect(status().isNotFound()).andReturn();

    }

    @Test
    public void createRoute() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        doNothing().when(travelDAO).update(any());

        Travel travel = user.getTravels().stream().findAny().orElse(null);

        ObjectNode objectNode = generateAPIRouteJsonNode();

        MvcResult result = performRequest(
                HttpMethod.POST,
                getRouteIndexEndpoint(user.getIdentifier().toString(), travel.getIdentifier().toString()),
                objectNode.toString(),
                null
        ).andExpect(status().isCreated()).andReturn();

        verify(routeDAO, times(1)).insert(any());

        JsonNode route = toJsonNode(result);
        assertIsValidRoute(route);
    }

    @Test
    public void createRouteNoUser() throws Exception {
        when(userDAO.find(any())).thenReturn(null);

        ObjectNode objectNode = generateAPIRouteJsonNode();

        performRequest(
                HttpMethod.POST,
                getRouteIndexEndpoint(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                objectNode.toString(),
                null
        ).andExpect(status().isNotFound()).andReturn();

        verify(routeDAO, never()).insert(any());
    }

    @Test
    public void createRouteNoTravel() throws Exception {
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        ObjectNode objectNode = generateAPIRouteJsonNode();

        performRequest(
                HttpMethod.POST,
                getRouteIndexEndpoint(user.getIdentifier().toString(), UUID.randomUUID().toString()),
                objectNode.toString(),
                null
        ).andExpect(status().isNotFound());

        verify(routeDAO, never()).insert(any());
    }

}
