package be.ugent.vopro5.backend.businesslayer.applicationfacade.auth;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.login.FacebookLoginProvider;
import be.ugent.vopro5.backend.util.ObjectGeneration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by thibault on 4/13/16.
 */
public class RefreshTokenControllerUnitTest extends ControllerTest {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private FacebookLoginProvider facebookLoginProvider;

    @Test
    public void testRegularLoginUser() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "password";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        User user = ObjectGeneration.generateUser();
        user.setPassword(SCryptUtil.scrypt(password, 16384, 8, 1));

        when(userDAO.findByEmail(any())).thenReturn(user);

        MvcResult mvcResult = performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = toJsonNode(mvcResult);
        assertTrue(result.get("user_id").isTextual());
        assertTrue(result.get("token").isTextual());
        assertTrue(result.get("user_url").isTextual());
        assertTrue(result.get("role").isTextual());
        assertEquals(result.get("role").asText(), "USER");
    }

    @Test
    public void testRegularLoginUserWrongPassword() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "password";

        node.put("email", "test_email@ugent.be");
        node.put("password", "wrong_password");

        User user = ObjectGeneration.generateUser();
        user.setPassword(SCryptUtil.scrypt(password, 16384, 8, 1));

        when(userDAO.findByEmail(any())).thenReturn(user);

        performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegularLoginOperator() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "password";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        Operator operator = ObjectGeneration.generateOperator();
        operator.setPassword(SCryptUtil.scrypt(password, 16384, 8, 1));

        when(operatorDAO.findByEmail(any())).thenReturn(operator);

        MvcResult mvcResult = performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = toJsonNode(mvcResult);
        assertTrue(result.get("user_id").isTextual());
        assertTrue(result.get("token").isTextual());
        assertTrue(result.get("user_url").isTextual());
        assertTrue(result.get("role").isTextual());
        assertEquals(result.get("role").asText(), "OPERATOR");
    }

    @Test
    public void testRegularLoginOperatorWrongPassword() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "password";

        node.put("email", "test_email@ugent.be");
        node.put("password", "wrong_password");

        Operator operator = ObjectGeneration.generateOperator();
        operator.setPassword(SCryptUtil.scrypt(password, 16384, 8, 1));

        when(operatorDAO.findByEmail(any())).thenReturn(operator);

        performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegularLoginAdmin() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("email", adminEmail);
        node.put("password", adminPassword);

        MvcResult mvcResult = performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = toJsonNode(mvcResult);
        assertTrue(result.get("user_id").isTextual());
        assertTrue(result.get("token").isTextual());
        assertTrue(result.get("user_url").isTextual());
        assertTrue(result.get("role").isTextual());
        assertEquals(result.get("role").asText(), "ADMIN");
    }

    @Test
    public void testRegularLoginAdminWrongPassword() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("email", adminEmail);
        node.put("password", adminPassword + "some_random_stuff");

        performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNotExisting() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("email", "nonexisting@ugent.be");
        node.put("password", "some_random_stuff");

        when(operatorDAO.findByEmail(any())).thenReturn(null);
        when(userDAO.findByEmail(any())).thenReturn(null);

        performRequest(HttpMethod.POST, "/refresh_token/regular", node.toString(), null)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFacebookLogin() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "some_facebook_identifier";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        User user = ObjectGeneration.generateUser();
        user.setPassword(SCryptUtil.scrypt(password, 16384, 8, 1));

        when(userDAO.findByEmail(any())).thenReturn(user);

        com.restfb.types.User facebookUser = new com.restfb.types.User();
        facebookUser.setEmail("test_email@ugent.be");
        facebookUser.setId(password);
        when(facebookLoginProvider.getFacebookUserFromCode(any(), any())).thenReturn(facebookUser);

        MvcResult mvcResult = performRequest(HttpMethod.POST, "/refresh_token/facebook", node.toString(), null)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = toJsonNode(mvcResult);
        assertTrue(result.get("user_id").isTextual());
        assertTrue(result.get("token").isTextual());
        assertTrue(result.get("user_url").isTextual());
        assertTrue(result.get("role").isTextual());
        assertEquals(result.get("role").asText(), "USER");
    }

    @Test
    public void testFacebookLoginInvalidFacebookAnswer() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "some_facebook_identifier";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        com.restfb.types.User facebookUser = new com.restfb.types.User();
        // No ID or email
        when(facebookLoginProvider.getFacebookUserFromCode(any(), any())).thenReturn(facebookUser);

        performRequest(HttpMethod.POST, "/refresh_token/facebook", node.toString(), null)
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    public void testFacebookLoginInvalidFacebookAnswer2() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "some_facebook_identifier";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        com.restfb.types.User facebookUser = new com.restfb.types.User();
        facebookUser.setId(password);
        when(facebookLoginProvider.getFacebookUserFromCode(any(), any())).thenReturn(facebookUser);

        performRequest(HttpMethod.POST, "/refresh_token/facebook", node.toString(), null)
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    public void testFacebookLoginInvalidPassword() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "some_facebook_identifier";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        User user = ObjectGeneration.generateUser();
        user.setPassword(SCryptUtil.scrypt("other_password", 16384, 8, 1));

        when(userDAO.findByEmail(any())).thenReturn(user);

        com.restfb.types.User facebookUser = new com.restfb.types.User();
        facebookUser.setEmail("test_email@ugent.be");
        facebookUser.setId(password);
        when(facebookLoginProvider.getFacebookUserFromCode(any(), any())).thenReturn(facebookUser);

        performRequest(HttpMethod.POST, "/refresh_token/facebook", node.toString(), null)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFacebookLoginNewUser() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String password = "some_facebook_identifier";

        node.put("email", "test_email@ugent.be");
        node.put("password", password);

        when(userDAO.findByEmail(any())).thenReturn(null);
        doNothing().when(userDAO).insert(any());

        com.restfb.types.User facebookUser = new com.restfb.types.User();
        facebookUser.setEmail("test_email@ugent.be");
        facebookUser.setId(password);
        when(facebookLoginProvider.getFacebookUserFromCode(any(), any())).thenReturn(facebookUser);

        performRequest(HttpMethod.POST, "/refresh_token/facebook", node.toString(), null)
                .andExpect(status().isCreated());
    }
}
