package be.ugent.vopro5.backend.businesslayer.businessworkflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by evert on 4/12/16.
 */
public class GoogleCaptchaHelperTest {

    private ObjectMapper mockObjectMapper;
    private GoogleCaptchaHelper googleCaptchaHelper;

    @Before
    public void setup() {
        mockObjectMapper = Mockito.mock(ObjectMapper.class);
        googleCaptchaHelper = new GoogleCaptchaHelper(mockObjectMapper, "unused");
    }

    @Test
    public void verifyCaptcha() throws Exception {
        ObjectNode successNode = JsonNodeFactory.instance.objectNode();
        successNode.put("success", true);
        when(mockObjectMapper.readTree(eq(new URL("https://www.google.com/recaptcha/api/siteverify?secret=unused&response=authcode")))).thenReturn(successNode);

        assertTrue(googleCaptchaHelper.verifyCaptcha("authcode"));

        verify(mockObjectMapper, times(1)).readTree(any(URL.class));
    }

    @Test
    public void verifyCaptchaEmpty() throws Exception {
        assertFalse(googleCaptchaHelper.verifyCaptcha(null));
        assertFalse(googleCaptchaHelper.verifyCaptcha(""));

        verify(mockObjectMapper, never()).readTree(any(URL.class));
    }

    @Test
    public void verifyReturnNull() throws Exception {
        when(mockObjectMapper.readTree(eq(new URL("https://www.google.com/recaptcha/api/siteverify?secret=unused&response=authcode")))).thenReturn(null);

        assertFalse(googleCaptchaHelper.verifyCaptcha("authcode"));

        verify(mockObjectMapper, times(1)).readTree(any(URL.class));
    }

    @Test
    public void verifyReturnFalse() throws Exception {
        ObjectNode successNode = JsonNodeFactory.instance.objectNode();
        successNode.put("success", false);
        when(mockObjectMapper.readTree(eq(new URL("https://www.google.com/recaptcha/api/siteverify?secret=unused&response=authcode")))).thenReturn(successNode);

        assertFalse(googleCaptchaHelper.verifyCaptcha("authcode"));

        verify(mockObjectMapper, times(1)).readTree(any(URL.class));
    }

    @Test
    public void verifyThrowException() throws Exception {
        when(mockObjectMapper.readTree(eq(new URL("https://www.google.com/recaptcha/api/siteverify?secret=unused&response=authcode")))).thenThrow(new IOException());

        assertFalse(googleCaptchaHelper.verifyCaptcha("authcode"));

        verify(mockObjectMapper, times(1)).readTree(any(URL.class));
    }

}