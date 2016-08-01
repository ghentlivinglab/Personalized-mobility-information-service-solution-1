package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Person;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.AccessToken;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by thibault on 4/2/16.
 */
public class AccessTokenService {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${admin.identifier}")
    private String adminIdentifier;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private DataAccessProvider dataAccessProvider;

    /**
     * This method verifies the authorization of incoming API requests wherein a authorization-header is present. Whereas a GET to /user/{id}/
     * requires user-privileges a GET request to /user/ requires admin-privileges. The authorization is embedded into the authorization header of the request
     * and contains an access-token.
     * @param request The request to be verified.
     * @return The authentication corresponding to the request.
     */
    public Authentication getAuthentication(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || authorization.equals("") || !authorization.startsWith(BEARER)) {
            return null;
        }
        String token = authorization.replace(BEARER, "");
        AccessToken accessToken = AccessToken.parseToken(secret, token);
        if (accessToken == null) {
            return null;
        }
        User user = dataAccessProvider.getDataAccessContext().getUserDAO().find(accessToken.getSubject());
        Operator operator = dataAccessProvider.getDataAccessContext().getOperatorDAO().find(accessToken.getSubject());
        if (user != null) {
            return new AccessTokenAuthentication(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), user);
        } else if (operator != null) {
            return new AccessTokenAuthentication(Collections.singleton(new SimpleGrantedAuthority("ROLE_OPERATOR")), operator);
        } else if (accessToken.getSubject().equals(adminIdentifier)) {
            return new AccessTokenAuthentication(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")),
                    new Person(
                            UUID.randomUUID(),
                            new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, adminEmail, true),
                            adminPassword));
        } else {
            return null;
        }
    }

    /**
     * Checks if an authorization header is present in a request.
     * @param request The request to be checked.
     * @return
     */
    public boolean requestHasAuthentication(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER) != null;
    }

    /**
     * Authentication class for Spring Security.
     * Spring Security uses this to know which roles we connect to an authenticated actor.
     */
    private static class AccessTokenAuthentication extends AbstractAuthenticationToken {
        private final Person user;

        public AccessTokenAuthentication(Collection<? extends GrantedAuthority> authorities, Person user) {
            super(authorities);
            this.user = user;
            super.setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return user.getIdentifier();
        }

        @Override
        public Object getPrincipal() {
            return user;
        }
    }
}
