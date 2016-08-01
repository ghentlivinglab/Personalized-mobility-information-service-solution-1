package be.ugent.vopro5.backend.businesslayer.applicationfacade.auth;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.util.RefreshToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by thibault on 4/13/16.
 */
public class AccessTokenControllerUnitTest extends ControllerTest {
    @Value("${jwt.secret}")
    private String secret;

    @Test
    public void testCreateAccessToken() throws Exception {
        RefreshToken refreshToken = new RefreshToken("some_user_id");

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("refresh_token", refreshToken.toToken(secret));

        MvcResult mvcResult = performRequest(HttpMethod.POST, "/access_token/", node.toString(), null)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = toJsonNode(mvcResult);
        assertTrue(result.get("token").isTextual());
        assertTrue(result.get("exp").isTextual());
    }

    @Test
    public void invalidRefreshToken() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("refresh_token", "invalid_refresh_token");
        performRequest(HttpMethod.POST, "/access_token/", node.toString(), null)
                .andExpect(status().isUnauthorized());
    }
}
