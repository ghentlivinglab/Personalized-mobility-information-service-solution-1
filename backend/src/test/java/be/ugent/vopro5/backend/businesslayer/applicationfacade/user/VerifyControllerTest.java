package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import java.util.UUID;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by evert on 4/13/16.
 */
public class VerifyControllerTest extends ControllerTest {
    @Test
    public void verifyCorrect() throws Exception {
        User user = generateUser();
        when(userDAO.find(any())).thenReturn(user);

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("email_verification_pin", user.getEmailVerification());

        performRequest(
                HttpMethod.PUT,
                "/user/" + UUID.randomUUID() + "/verify", node.toString(), null
        ).andExpect(status().isOk());

        assertTrue(user.getEmailisValidated());
        verify(userDAO, times(1)).update(user);
    }

    @Test
    public void verifyIncorrect() throws Exception {
        User user = generateUser();
        user.setEmail("a@b.com");
        when(userDAO.find(any())).thenReturn(user);

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("email_verification_pin", user.getEmailVerification() + "aa");

        performRequest(
                HttpMethod.PUT,
                "/user/" + UUID.randomUUID() + "/verify", node.toString(), null
        ).andExpect(status().isBadRequest());

        assertFalse(user.getEmailisValidated());
        verify(userDAO, never()).update(user);
    }

    @Test
    public void verifyCellNumber() throws Exception {
        User user = generateUser();
        user.setEmail("a@b.com");
        when(userDAO.find(any())).thenReturn(user);

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("cell_number_verification_pin", "adjksad");

        performRequest(
                HttpMethod.PUT,
                "/user/" + UUID.randomUUID() + "/verify", node.toString(), null
        ).andExpect(status().isOk());

        assertFalse(user.getEmailisValidated());
        verify(userDAO, never()).update(user);
    }

    @Test
    public void verifyNoUser() throws Exception {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("email_verification_pin", "1908");

        performRequest(
                HttpMethod.PUT,
                "/user/" + UUID.randomUUID() + "/verify", node.toString(), null
        ).andExpect(status().isNotFound());
    }

}