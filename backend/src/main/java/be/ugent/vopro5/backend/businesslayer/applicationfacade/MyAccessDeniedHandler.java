package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.util.Error;
import be.ugent.vopro5.backend.businesslayer.util.mapping.MyObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by thibault on 4/4/16.
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ObjectMapper mapper = new MyObjectMapper();
        mapper.writeValue(response.getOutputStream(), new Error(403, accessDeniedException.getMessage(), Collections.emptyList()));
    }
}
