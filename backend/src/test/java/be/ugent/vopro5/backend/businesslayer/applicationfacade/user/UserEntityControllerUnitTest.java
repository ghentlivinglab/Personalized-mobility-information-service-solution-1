package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.USER_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Fields.EMAIL_FIELD;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidError;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidUser;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIUserJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 28/02/16.
 */
public class UserEntityControllerUnitTest extends ControllerTest {

    private static final String IMAGINARY_USER = "imaginary_user";

    private String getUserEntityEndpoint(String id) {
        return USER_INDEX_ENDPOINT + id + "/";
    }

    @Test
    public void testGetUserNotExist() throws Exception {
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        // This user does not exist

        JsonNode node = performGetRequestNotFound(getUserEntityEndpoint(IMAGINARY_USER));
        assertIsValidError(node);
    }

    /**
     * Test the method getUser in both successful and unsuccessful scenarios.
     * In successful case check whether necessary fields are present and correct.
     *
     * @throws Exception
     */
    @Test
    public void testGetUser() throws Exception {
        User user = generateUser();
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);

        JsonNode node = performGetRequestNoContent(
                getUserEntityEndpoint(user.getIdentifier().toString())
        );
        assertIsValidUser(node);
    }

    /**
     * Test the method deleteUser in both successful and unsuccessful scenarios.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteUser() throws Exception {
        User user = generateUser();
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user).thenReturn(null);

        // Delete it
        performDeleteNoContent(
                getUserEntityEndpoint(user.getIdentifier().toString())
        );
        // Can't exist anymore now
        performDeleteNotFound(getUserEntityEndpoint(user.getIdentifier().toString()));
    }

    @Test
    public void testUserUpdateNotExist() throws Exception {
        when(userDAO.find(IMAGINARY_USER)).thenReturn(null);
        // This user does not exist
        JsonNode node = toJsonNode(performRequest(
                HttpMethod.PUT,
                getUserEntityEndpoint(IMAGINARY_USER),
                generateAPIUserJsonNode(true).toString(), null)
                .andExpect(status().isNotFound())
                .andReturn());
        assertIsValidError(node);
    }

    /**
     * Test the method updateUser in both successful, unsuccessful, and conflicting scenarios.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateUser() throws Exception {
        User user = generateUser();
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        doNothing().when(userDAO).update(any());

        // Update the user with a new email
        SecurityContextHolder.getContext().setAuthentication(new AbstractAuthenticationToken(Collections.singleton(new SimpleGrantedAuthority("USER"))) {
            @Override
            public Object getCredentials() {
                return user.getEmail();
            }

            @Override
            public Object getPrincipal() {
                return user;
            }
        });
        JsonNode node = toJsonNode(
                performRequest(HttpMethod.PUT,
                        getUserEntityEndpoint(user.getIdentifier().toString()),
                        generateAPIUserJsonNode(true).toString(),
                        null).andExpect(status().isOk()).andReturn()
        );

        assertIsValidUser(node);
    }

    @Test
    public void testUpdateUserNoEmailChange() throws Exception {
        User user = generateUser();
        when(userDAO.find(user.getIdentifier().toString())).thenReturn(user);
        doNothing().when(userDAO).update(any());

        ObjectNode userNode = generateAPIUserJsonNode(true);
        userNode.put("email", user.getEmail());

        // Update the user with no new email
        JsonNode node = toJsonNode(
                performRequest(HttpMethod.PUT,
                        getUserEntityEndpoint(user.getIdentifier().toString()),
                        userNode.toString(),
                        null).andExpect(status().isOk()).andReturn()
        );

        assertIsValidUser(node);
    }

    @Test
    public void testUpdateUserDuplicateEmail() throws Exception {
        User alreadyExistingUser = generateUser();
        when(userDAO.findByEmail(alreadyExistingUser.getEmail())).thenReturn(alreadyExistingUser);

        // Update the user with an email already in use by another user
        ObjectNode userJsonNode = generateAPIUserJsonNode(true);
        userJsonNode.put(EMAIL_FIELD, alreadyExistingUser.getEmail());
        User updatingUser = generateUser();
        when(userDAO.find(updatingUser.getIdentifier().toString())).thenReturn(updatingUser);

        JsonNode node = toJsonNode(performRequest(
                HttpMethod.PUT,
                getUserEntityEndpoint(updatingUser.getIdentifier().toString()),
                userJsonNode.toString(),
                null)
                .andExpect(status().isConflict())
                .andReturn());
        assertIsValidError(node);
    }


}