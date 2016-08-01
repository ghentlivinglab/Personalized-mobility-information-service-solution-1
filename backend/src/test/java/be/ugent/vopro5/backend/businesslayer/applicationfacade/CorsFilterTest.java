package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by thibault on 3/13/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/WEB-INF/test-servlet.xml")
public class CorsFilterTest {
    @Test
    public void testHeadersAllowCORS() throws ServletException, IOException {
        CORSFilter filter = new CORSFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        request.addHeader("Access-Control-Request-Headers", "Authorization");

        filter.init(new MockFilterConfig());
        filter.doFilter(request, response, chain);
        filter.destroy();

        assertEquals(response.getHeader("Access-Control-Allow-Origin"), "*");
        assertEquals(response.getHeader("Access-Control-Allow-Methods"), "PUT, POST, GET, OPTIONS, DELETE");
        assertEquals(response.getHeader("Access-Control-Allow-Headers"), request.getHeader("Access-Control-Request-Headers"));
    }
}
