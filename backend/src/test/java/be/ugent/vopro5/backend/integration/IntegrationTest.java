package be.ugent.vopro5.backend.integration;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessobjects.MongoDataAccessContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.USER_INDEX_ENDPOINT;
import static jdk.internal.dynalink.CallSiteDescriptor.OPERATOR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by evert on 4/3/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/WEB-INF/integration-servlet.xml")
public abstract class IntegrationTest {

    @Autowired
    protected MappingJackson2HttpMessageConverter converter;
    @Autowired
    protected ObjectMapper mapper;
    protected MockMvc mockMvc;
    protected Random random = new Random();
    @Autowired
    protected DataAccessProvider dataAccessProvider;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ConcurrencyController concurrencyController;

    @Value("${admin.identifier}")
    protected String adminIdentifier;

    @Value("${admin.email}")
    protected String adminEmail;

    @Value("${admin.password}")
    protected String adminPassword;

    protected String adminAccessToken;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        ObjectNode emailPasswordBody = JsonNodeFactory.instance.objectNode();
        emailPasswordBody.put("email",adminEmail);
        emailPasswordBody.put("password",adminPassword);
        adminAccessToken = getBearerToken(emailPasswordBody);
    }

    @After
    public void tearDown() {
        ((MongoDataAccessContext) dataAccessProvider.getDataAccessContext()).dropDB();
    }

    protected ResultActions performRequest(HttpMethod method, String endpoint, String content, Map<String, String> params, String bearerToken) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, endpoint);

        if(bearerToken != null) {
            builder.header(HttpHeaders.AUTHORIZATION,bearerToken);
        }

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

    protected JsonNode toJsonNode(MvcResult result) throws IOException {
        return mapper.readTree(result.getResponse().getContentAsString());
    }

    protected AccessDetails createPersonAndGetAccessDetails(ObjectNode personNode,String endpoint,boolean adminAccessNeeded) throws Exception {
        ObjectNode emailPasswordBody = JsonNodeFactory.instance.objectNode();
        MvcResult mvcCreatedPersonResult = null;

        if(adminAccessNeeded) {
             mvcCreatedPersonResult = performRequest(HttpMethod.POST, endpoint, personNode.toString(), null, adminAccessToken)
                    .andExpect(status().isCreated()).andReturn();
        } else {
             mvcCreatedPersonResult = performRequest(HttpMethod.POST, endpoint, personNode.toString(), null, null)
                    .andExpect(status().isCreated()).andReturn();
        }

        String personId = toJsonNode(mvcCreatedPersonResult).get("id").asText();

        emailPasswordBody.set("email",personNode.get("email"));
        emailPasswordBody.set("password",personNode.get("password"));

        String bearerToken = getBearerToken(emailPasswordBody);

        return new AccessDetails(bearerToken,personId,toJsonNode(mvcCreatedPersonResult));
    }

    private String getBearerToken(ObjectNode emailPasswordBody) throws Exception {
        //obtain an access token
        MvcResult refreshTokenResult = performRequest(HttpMethod.POST, "/refresh_token/regular", emailPasswordBody.toString(), null,null)
                .andExpect(status().isOk()).andReturn();

        ObjectNode refreshToken = JsonNodeFactory.instance.objectNode();
        refreshToken.set("refresh_token",toJsonNode(refreshTokenResult).get("token"));

        //obtain an access token
        MvcResult accessTokenResult = performRequest(HttpMethod.POST, "/access_token/", refreshToken.toString(), null,null)
                .andExpect(status().isOk()).andReturn();

        return "Bearer " + mapper.treeToValue(toJsonNode(accessTokenResult).get("token"), String.class);
    }

    protected class AccessDetails {
        private String accessToken;
        private String userId;
        private JsonNode user;

        protected AccessDetails(String bearerToken, String userId, JsonNode user) {
            this.accessToken = bearerToken;
            this.userId = userId;
            this.user = user;
        }

        protected String getAccessToken() {
            return accessToken;
        }

        protected String getUserId() {
            return userId;
        }

        protected JsonNode getUser() {
            return user;
        }
    }
}
