package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.util.Error;
import be.ugent.vopro5.backend.businesslayer.util.mapping.MyObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by thibault on 4/2/16.
 */
public class MyUnauthorizedHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Since we use REST, we don't need to go through some login process. The frontend should take care of that on it's own.
        // We simply return "You aren't authenticated".
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token.");
    }
}
