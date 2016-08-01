package be.ugent.vopro5.backend.businesslayer.applicationfacade.travel.route;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Travel;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getRouteEntityEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidRoute;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by anton on 3/27/16.
 */
public class RouteEntityControllerUnitTest extends ControllerTest {

    @Test
    public void testGetRoute() throws Exception {
        User user = generateUser();
        Route route = generateRoute();

        Travel travel = generateTravel();
        travel.addRoute(route);

        user.addTravel(travel);
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        JsonNode node = performGetRequestNoContent(
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        travel.getIdentifier().toString(),
                        travel.getRoutes().iterator().next().getIdentifier().toString()
                )
        );
        assertIsValidRoute(node);
    }

    @Test
    public void testGetRouteUserDoesNotExist() throws Exception {
        Travel travel = generateTravel();
        when(userDAO.find("")).thenReturn(null);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);

        performRequest(HttpMethod.GET, getRouteEntityEndpoint("", travel.getIdentifier().toString(),""), null, null)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRouteTravelDoesNotExist() throws Exception {
        User user = generateUser();
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find("")).thenReturn(null);

        performGetRequestNotFound(getRouteEntityEndpoint(user.getIdentifier().toString(),
                "",""));
    }

    @Test
    public void testGetRouteDoesNotExist() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);

        user.addTravel(travel);

        performGetRequestNotFound(
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        travel.getIdentifier().toString(),
                        IMAGINARY_ROUTE)
        );
    }

    @Test
    public void testUpdateRoute() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        Route route = generateRoute();
        travel.addRoute(route);
        user.addTravel(travel);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);

        performRequest(
                HttpMethod.PUT,
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        travel.getIdentifier().toString(),
                        route.getIdentifier().toString()),
                generateAPIRouteJsonNode().toString(),
                null).andExpect(status().isOk());

        verify(routeDAO, times(1)).update(any());
    }


    @Test
    public void testUpdateRouteUserNotFound() throws Exception {
        Travel travel = generateTravel();
        Route route = generateRoute();
        travel.addRoute(route);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);
        when(routeDAO.find(route.getIdentifier().toString())).thenReturn(route);
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);

        performRequest(
                HttpMethod.PUT,
                getRouteEntityEndpoint(IMAGINARY_USER, travel.getIdentifier().toString(),route.getIdentifier().toString()),
                generateAPIRouteJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateRouteTravelNotFound() throws Exception {
        Route route = generateRoute();
        User user = generateUser();
        when(travelDAO.find(IMAGINARY_TRAVEL)).thenReturn(null);
        when(routeDAO.find(route.getIdentifier().toString())).thenReturn(route);
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        performRequest(
                HttpMethod.PUT,
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        IMAGINARY_TRAVEL,
                        route.getIdentifier().toString()
                ),
                generateAPIRouteJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateRouteNotFound() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        user.addTravel(travel);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);
        when(routeDAO.find(IMAGINARY_ROUTE)).thenReturn(null);
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        performRequest(
                HttpMethod.PUT,
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        travel.getIdentifier().toString(),
                        IMAGINARY_ROUTE),
                generateAPIRouteJsonNode().toString(),
                null).andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRoute() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        Route route = generateRoute();

        travel.addRoute(route);
        user.addTravel(travel);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);
        when(routeDAO.find(route.getIdentifier().toString())).thenReturn(route);
        doNothing().when(userDAO).update(any());
        doNothing().when(travelDAO).update(any());

        performDeleteNoContent(
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        travel.getIdentifier().toString(),
                        route.getIdentifier().toString()
                )
        );

        verify(routeDAO, times(1)).delete(any());
        verify(travelDAO, times(1)).update(any());
    }

    @Test
    public void testDeleteRouteNotFound() throws Exception {
        User user = generateUser();
        Travel travel = generateTravel();
        user.addTravel(travel);

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);
        when(routeDAO.find(IMAGINARY_ROUTE)).thenReturn(null);

        performDeleteNotFound(
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        travel.getIdentifier().toString(),
                        IMAGINARY_ROUTE)
        );

        verify(routeDAO, times(0)).delete(any());
    }

    @Test
    public void testDeleteRouteUserNotFound() throws Exception {
        Route route = generateRoute();
        Travel travel = generateTravel();
        travel.addRoute(route);

        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        when(travelDAO.find(travel.getIdentifier().toString())).thenReturn(travel);
        when(routeDAO.find(route.getIdentifier().toString())).thenReturn(route);

        performDeleteNotFound(
                getRouteEntityEndpoint(
                        IMAGINARY_USER,
                        travel.getIdentifier().toString(),
                        route.getIdentifier().toString())
        );

        verify(routeDAO, times(0)).delete(any());
    }


    public void testDeleteRouteTravelNotFound() throws Exception {
        Route route = generateRoute();
        User user = generateUser();

        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        when(travelDAO.find(IMAGINARY_TRAVEL)).thenReturn(null);
        when(routeDAO.find(route.getIdentifier().toString())).thenReturn(route);

        performDeleteNotFound(
                getRouteEntityEndpoint(
                        user.getIdentifier().toString(),
                        IMAGINARY_TRAVEL,
                        route.getIdentifier().toString())
        );

        verify(routeDAO, times(0)).delete(any());
    }
}
