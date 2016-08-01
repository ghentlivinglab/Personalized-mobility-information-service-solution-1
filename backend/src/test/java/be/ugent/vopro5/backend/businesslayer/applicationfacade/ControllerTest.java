package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.mock.MockDataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.AbstractMap;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by anton on 4/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/WEB-INF/test-servlet.xml")
public abstract class ControllerTest {

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected DataAccessProvider dataAccessProvider;

    @Autowired
    protected JavaMailSender mailSender;

    protected MockDataAccessContext dac;
    protected UserDAO userDAO;
    protected EventDAO eventDAO;
    protected TravelDAO travelDAO;
    protected PointOfInterestDAO poiDAO;
    protected RouteDAO routeDAO;
    protected OperatorDAO operatorDAO;
    protected MockMvc mockMvc;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    protected static final String IMAGINARY_USER = "imaginary_user";
    protected static final String IMAGINARY_TRAVEL = "imaginary_travel";
    protected static final String IMAGINARY_ROUTE = "IMAGINARY_ROUTE";

    /**
     * Set up all ControllerTests by initializing the mock environment
     * in which these tests take place. Also set up the converter and
     * corresponding ObjectMapper used for translating between object
     * and json formats.
     */
    @Before
    public void setup() {
        mockMvc = webAppContextSetup(wac).build();

        dac = spy(new MockDataAccessContext());
        when(dataAccessProvider.getDataAccessContext()).thenReturn(dac);

        userDAO = spy(dac.getUserDAO());
        when(dac.getUserDAO()).thenReturn(userDAO);
        eventDAO = spy(dac.getEventDAO());
        when(dac.getEventDAO()).thenReturn(eventDAO);
        travelDAO = spy(dac.getTravelDAO());
        when(dac.getTravelDAO()).thenReturn(travelDAO);
        poiDAO = spy(dac.getPointOfInterestDAO());
        when(dac.getPointOfInterestDAO()).thenReturn(poiDAO);
        routeDAO = spy(dac.getRouteDAO());
        when(dac.getRouteDAO()).thenReturn(routeDAO);
        operatorDAO = spy(dac.getOperatorDAO());
        when(dac.getOperatorDAO()).thenReturn(operatorDAO);

        reset(mailSender);
    }

    @After
    public void tearDown() {
        reset(userDAO);
        reset(eventDAO);
        reset(travelDAO);
        reset(dac);
    }

    /**
     * @param method   The HTTP method to use (ex. post, put, get)
     * @param endpoint The endpoint to perform the request to
     * @param content  The content to send
     * @param params
     * @return Returns builder to extend request with expectations (ex. andExpect)
     * @throws Exception
     */
    protected ResultActions performRequest(HttpMethod method, String endpoint, String content, AbstractMap<String, String> params) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, endpoint);

        if (content != null) {
            builder.content(content)
                    .contentType(MediaType.APPLICATION_JSON);

        }
        if (params != null) {
            for (String key : params.keySet()) {
                builder.param(key, params.get(key));
            }
        }
        return mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON));
    }

    protected JsonNode performGetRequestNoContent(String endpoint) throws Exception{
        MvcResult mvcResult = performRequest(
                HttpMethod.GET,
                endpoint,
                null,
                null).andExpect(status().isOk()).andReturn();

        JsonNode node = toJsonNode(mvcResult);

        return node;
    }

    protected JsonNode performGetRequestNotFound(String endpoint) throws Exception{
        MvcResult mvcResult = performRequest(
                HttpMethod.GET,
                endpoint,
                null,
                null
        ).andExpect(status().isNotFound()).andReturn();

        JsonNode node = toJsonNode(mvcResult);

        return node;
    }

    protected void performDeleteNotFound(String endpoint) throws Exception{
        performRequest(
                HttpMethod.DELETE,
                endpoint,
                null,
                null).andExpect(status().isNotFound());
    }

    protected void performDeleteNoContent(String endpoint) throws Exception{
        performRequest(
                HttpMethod.DELETE,
                endpoint,
                null,
                null).andExpect(status().isNoContent());
    }

    /**
     * @param method   The HTTP method to use (ex. post, put, get)
     * @param endpoint The endpoint to perform the request to
     * @param content  The content to send
     * @param params
     * @return Returns builder to extend request with expectations (ex. andExpect)
     * @throws Exception
     */
    protected ResultActions performRequestWithHeader(HttpMethod method, String endpoint, String content, AbstractMap<String, String> params, AbstractMap<String, String> header) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, endpoint);

        if (content != null) {
            builder.content(content)
                    .contentType(MediaType.APPLICATION_JSON);

        }
        if (params != null) {
            for (String key : params.keySet()) {
                builder.param(key, params.get(key));
            }
        }

        if (header != null) {
            for (String key : header.keySet()) {
                builder.header(key, header.get(key));
            }
        }

        return mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON));
    }

    /**
     * @param result The result from a mockrequest
     * @return Returns the JSON body of the request
     * @throws IOException
     */
    protected JsonNode toJsonNode(MvcResult result) throws IOException {
        return jacksonObjectMapper.readTree(result.getResponse().getContentAsString());
    }
}
