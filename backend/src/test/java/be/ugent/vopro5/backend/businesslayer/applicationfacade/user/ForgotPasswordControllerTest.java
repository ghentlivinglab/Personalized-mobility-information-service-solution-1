package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by evert on 4/13/16.
 */
public class ForgotPasswordControllerTest extends ControllerTest {

    @Test
    public void forgotPassword() throws Exception {
        when(userDAO.findByEmail("a@b.com")).thenReturn(generateUser());

        ObjectNode emailNode = JsonNodeFactory.instance.objectNode();
        emailNode.put("email", "a@b.com");

        performRequest(
                HttpMethod.POST,
                "/user/forgot_password",
                emailNode.toString(),
                null
        ).andExpect(status().isOk());

        verify(mailSender, times(1)).send(any(MimeMessagePreparator.class));
    }

    @Test
    public void forgotPasswordNoUser() throws Exception {
        when(userDAO.findByEmail(any())).thenReturn(null);

        ObjectNode emailNode = JsonNodeFactory.instance.objectNode();
        emailNode.put("email", "a@b.com");

        performRequest(
                HttpMethod.POST,
                "/user/forgot_password",
                emailNode.toString(),
                null
        ).andExpect(status().isOk());

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void forgotPasswordException() throws Exception {
        when(userDAO.findByEmail("a@b.com")).thenReturn(generateUser());
        doThrow(new MailSendException("")).when(mailSender).send(any(MimeMessagePreparator.class));

        ObjectNode emailNode = JsonNodeFactory.instance.objectNode();
        emailNode.put("email", "a@b.com");

        performRequest(
                HttpMethod.POST,
                "/user/forgot_password",
                emailNode.toString(),
                null
        ).andExpect(status().isInternalServerError());

        verify(mailSender, times(1)).send(any(MimeMessagePreparator.class));
    }
}