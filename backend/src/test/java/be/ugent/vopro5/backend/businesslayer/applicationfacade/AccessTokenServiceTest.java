package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.RefreshToken;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.OperatorDAO;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertOperatorsEqual;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUsersEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateOperator;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by evert on 4/11/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/WEB-INF/test-servlet.xml")
public class AccessTokenServiceTest {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private WebApplicationContext wac;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${admin.identifier}")
    private String adminIdentifier;

    private UserDAO userDAO;
    private OperatorDAO operatorDAO;

    @Before
    public void setUp() throws Exception {
        DataAccessContext dataAccessContext = Mockito.mock(DataAccessContext.class);
        when(dataAccessProvider.getDataAccessContext()).thenReturn(dataAccessContext);
        userDAO = Mockito.mock(UserDAO.class);
        when(dataAccessContext.getUserDAO()).thenReturn(userDAO);
        operatorDAO = Mockito.mock(OperatorDAO.class);
        when(dataAccessContext.getOperatorDAO()).thenReturn(operatorDAO);
    }

    @Test
    public void getAuthenticationUser() throws Exception {
        User user = generateUser();
        when(userDAO.find(eq(user.getIdentifier().toString()))).thenReturn(user);
        MockHttpServletRequest mockHttpServletRequest = get("test").header("Authorization", "Bearer " + new RefreshToken(user.getIdentifier().toString()).toToken(secret)).buildRequest(wac.getServletContext());
        Authentication authentication = accessTokenService.getAuthentication(mockHttpServletRequest);
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
        assertEquals(user.getIdentifier().toString(), authentication.getCredentials().toString());
        assertUsersEqual(user, (User) authentication.getPrincipal());
    }

    @Test
    public void getAuthenticationOperator() throws Exception {
        Operator operator = generateOperator();
        when(operatorDAO.find(eq(operator.getIdentifier().toString()))).thenReturn(operator);
        MockHttpServletRequest mockHttpServletRequest = get("test").header("Authorization", "Bearer " + new RefreshToken(operator.getIdentifier().toString()).toToken(secret)).buildRequest(wac.getServletContext());
        Authentication authentication = accessTokenService.getAuthentication(mockHttpServletRequest);
        assertEquals("ROLE_OPERATOR", authentication.getAuthorities().iterator().next().getAuthority());
        assertEquals(operator.getIdentifier().toString(), authentication.getCredentials().toString());
        assertOperatorsEqual(operator, (Operator) authentication.getPrincipal());
    }

    @Test
    public void getAuthenticationNobody() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = get("test").buildRequest(wac.getServletContext());
        assertNull(accessTokenService.getAuthentication(mockHttpServletRequest));

        mockHttpServletRequest = get("test").header("Authorization", "").buildRequest(wac.getServletContext());
        assertNull(accessTokenService.getAuthentication(mockHttpServletRequest));

        mockHttpServletRequest = get("test").header("Authorization", "NotBearer fjlkdsjflkds").buildRequest(wac.getServletContext());
        assertNull(accessTokenService.getAuthentication(mockHttpServletRequest));

        mockHttpServletRequest = get("test").header("Authorization", "Bearer fjlkdsjflkds").buildRequest(wac.getServletContext());
        assertNull(accessTokenService.getAuthentication(mockHttpServletRequest));

        mockHttpServletRequest = get("test").header("Authorization", "Bearer " + new RefreshToken("djfslkjfds").toToken(secret)).buildRequest(wac.getServletContext());
        assertNull(accessTokenService.getAuthentication(mockHttpServletRequest));

    }

    @Test
    public void getAuthenticationAdmin() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = get("test").header("Authorization", "Bearer " + new RefreshToken(adminIdentifier).toToken(secret)).buildRequest(wac.getServletContext());
        Authentication authentication = accessTokenService.getAuthentication(mockHttpServletRequest);
        assertEquals("ROLE_ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void requestHasAuthentication() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = get("test").header("Authorization", "Bearer alksjdlsajdlksa").buildRequest(wac.getServletContext());
        assertTrue(accessTokenService.requestHasAuthentication(mockHttpServletRequest));

        mockHttpServletRequest = get("test").buildRequest(wac.getServletContext());
        assertFalse(accessTokenService.requestHasAuthentication(mockHttpServletRequest));
    }

}