package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.USER_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Fields.*;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidError;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidUser;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 25/02/16.
 */
public class UserIndexControllerUnitTest extends ControllerTest {

    /**
     * Test the method createUser in a successful scenarios.
     * Check whether necessary fields are present and correct.
     *
     * @throws Exception
     */
    @Test
    public void testCreateUser() throws Exception {

        MvcResult mvcResult = performRequestWithHeader(HttpMethod.POST, USER_INDEX_ENDPOINT, generateAPIUserJsonNode(true).toString(),
                null, generateAuthorizationHeader())
                .andExpect(status().isCreated())
                .andReturn();
        assertIsValidUser(toJsonNode(mvcResult));

        verify(userDAO, times(1)).insert(any());
    }

    /**
     * Test the method createUser when no (or an invalid) password is passed.
     * This should result in a 400 error code.
     *
     * @throws Exception
     */
    @Test
    public void testCreateUserNoPassword() throws Exception {
        ObjectNode user = generateAPIUserJsonNode(true);
        user.remove(PASSWORD_FIELD);
        // No password given
        performRequest(HttpMethod.POST, USER_INDEX_ENDPOINT, user.toString(), null)
                .andExpect(status().isBadRequest());
        verify(userDAO, times(0)).insert(any());

        // Empty password field
        user.put(PASSWORD_FIELD, "");
        performRequest(HttpMethod.POST, USER_INDEX_ENDPOINT, user.toString(), null)
                .andExpect(status().isBadRequest());
        verify(userDAO, times(0)).insert(any());
    }

    /**
     * Test the method createUser when no request body.
     * This should result in a 400 error code.
     *
     * @throws Exception
     */
    @Test
    public void testCreateUserNoBody() throws Exception {
        // Empty JSON
        performRequest(HttpMethod.POST, USER_INDEX_ENDPOINT, "{}", null)
                .andExpect(status().isBadRequest());
        // No body
        performRequest(HttpMethod.POST, USER_INDEX_ENDPOINT, "", null)
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the method createUser in an unsuccessful scenarios where the User to be created holds an email that
     * already exists.
     * Check whether necessary fields are present and correct.
     *
     * @throws Exception
     */
    @Test
    public void testCreateUserDuplicateEmail() throws Exception {
        // Create the first user
        JsonNode user = generateAPIUserJsonNode(true);
        MvcResult mvcResult = performRequestWithHeader(HttpMethod.POST, USER_INDEX_ENDPOINT, user.toString(), null,
                generateAuthorizationHeader())
                .andExpect(status().isCreated())
                .andReturn();
        assertIsValidUser(toJsonNode(mvcResult));
        // The controller should have inserted a user into the database
        verify(userDAO, times(1)).insert(any());
        //Create the second user with the same email
        ObjectNode user2 = generateAPIUserJsonNode(true);
        user2.put(EMAIL_FIELD, user.get(EMAIL_FIELD).asText());
        MvcResult result = performRequestWithHeader(HttpMethod.POST, USER_INDEX_ENDPOINT, user2.toString(), null,
                generateAuthorizationHeader())
                .andExpect(status().isConflict())
                .andReturn();
        // The controller shouldn't add another user to the database
        // so the callcount should not have been modified
        verify(userDAO, times(1)).insert(any());

        JsonNode node = toJsonNode(result);
        assertIsValidError(node);

        assertEquals(HttpStatus.CONFLICT.value(), node.get(STATUS_FIELD).asInt());
        assertEquals(1, node.get(FIELDS_FIELD).size());
        assertEquals("email", node.get(FIELDS_FIELD).get(0).asText());
    }

    /**
     * Test the method getUsers in a successful scenario.
     * Check if the result has the correct format and size.
     *
     * @throws Exception
     */
    @Test
    public void testGetUsers() throws Exception {
        // Generate some random users
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = generateUser();
            users.add(user);
        }
        // Make sure these users are returned when  the controller asks the databse for some users
        when(userDAO.listAll()).thenReturn(users);
        // Perform the request
        JsonNode node = performGetRequestNoContent(USER_INDEX_ENDPOINT);
        // Verify the returned array is the same length as the array the database returned
        assertTrue(node.isArray());
        ArrayNode arrayNode = (ArrayNode) node;
        assertEquals(users.size(), arrayNode.size());
        // Verify that each user in this array is a valid user representation
        for (JsonNode userNode : arrayNode) {
            assertIsValidUser(userNode);
        }
    }

}
