package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created on 5/03/16.
 */
@Component
public class CORSFilter implements Filter {
    private static final String ACCES_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

    @Override
    public void init(javax.servlet.FilterConfig config) throws ServletException {
        // Nothing to do here
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getHeader(ACCES_CONTROL_REQUEST_HEADERS) != null) {
            response.setHeader("Access-Control-Allow-Headers", request.getHeader(ACCES_CONTROL_REQUEST_HEADERS));
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // Nothing to do here
    }

}
