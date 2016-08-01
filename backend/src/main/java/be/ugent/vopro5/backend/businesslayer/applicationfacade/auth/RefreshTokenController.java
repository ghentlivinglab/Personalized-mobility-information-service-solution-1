package be.ugent.vopro5.backend.businesslayer.applicationfacade.auth;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.operator.OperatorEntityController;
import be.ugent.vopro5.backend.businesslayer.applicationfacade.user.UserEntityController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.login.FacebookLoginProvider;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.login.GoogleLoginProvider;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.RefreshToken;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.lambdaworks.crypto.SCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by thibault on 4/3/16.
 */
@RestController
@RequestMapping("/refresh_token/")
public class RefreshTokenController {

    public static final String OPERATOR_ROLE = "OPERATOR";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String INVALLID_EMAIL_PASSWORD = "Invalid email/password combination";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.identifier}")
    private String adminIdentifier;

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private FacebookLoginProvider facebookLoginProvider;

    @Autowired
    private GoogleLoginProvider googleLoginProvider;

    /**
     * @param body The body contains the email and password of a person who wishes to log into the system.
     * @return A refresh-token is returned when the email and password are authorized. The obtained refresh-token can then
     * be used to receive an access-token.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "regular", method = RequestMethod.POST)
    public RefreshTokenResponse regularRefreshToken(@Valid @RequestBody EmailPasswordBody body) {
        User user = dataAccessProvider.getDataAccessContext().getUserDAO().findByEmail(body.getEmail());
        Operator operator = dataAccessProvider.getDataAccessContext().getOperatorDAO().findByEmail(body.getEmail());

        if (user != null) {
            // User exists
            if (!SCryptUtil.check(body.getPassword(), user.getPassword())) {
                throw new APIException.UnauthorizedException(INVALLID_EMAIL_PASSWORD, Arrays.asList(EMAIL, PASSWORD));
            }
            return new RefreshTokenResponse(
                    (new RefreshToken(user.getIdentifier().toString())).toToken(secret),
                    user.getIdentifier().toString(),
                    linkTo(methodOn(UserEntityController.class).getUser(user.getIdentifier().toString())).toUri().toString(),
                    USER_ROLE,
                    user.getEmailisValidated()
            );
        } else if (operator != null) {
            // Operator exists
            if (!SCryptUtil.check(body.getPassword(), operator.getPassword())) {
                throw new APIException.UnauthorizedException(INVALLID_EMAIL_PASSWORD, Arrays.asList(EMAIL, PASSWORD));
            }
            return new RefreshTokenResponse(
                    (new RefreshToken(operator.getIdentifier().toString())).toToken(secret),
                    operator.getIdentifier().toString(),
                    linkTo(methodOn(OperatorEntityController.class).getOperator(operator.getIdentifier().toString())).toUri().toString(),
                    OPERATOR_ROLE,
                    true
            );
        } else if (body.getEmail().equals(adminEmail) && body.getPassword().equals(adminPassword)) {
            // This is the admin
            return new RefreshTokenResponse(
                    (new RefreshToken(adminIdentifier)).toToken(secret),
                    adminIdentifier,
                    "", // Admin has no endpoint
                    ADMIN_ROLE,
                    true
            );
        } else {
            // Neither User, nor Operator, nor Admin matches these
            throw new APIException.UnauthorizedException(INVALLID_EMAIL_PASSWORD, Arrays.asList(EMAIL, PASSWORD));
        }
    }

    /**
     * @param body Contains data to initiate the auth flow for facebook.
     * @return A refresh-token that can be used to obtain an access-token.
     */
    @RequestMapping(value = "facebook", method = RequestMethod.POST)
    public ResponseEntity<RefreshTokenResponse> facebookRefreshToken(@Valid @RequestBody OAuthClientBody body) {
        com.restfb.types.User facebookUser = facebookLoginProvider.getFacebookUserFromCode(body.getRedirectUri(), body.getCode());
        // Extract the fields we need
        String userId = facebookUser.getId();
        String email = facebookUser.getEmail();

        if (userId == null || email == null) {
            throw new APIException.ServiceNotAvailableException("Unable to get email from facebook");
        }
        email = email.toLowerCase();

        User user = dataAccessProvider.getDataAccessContext().getUserDAO().findByEmail(email);
        boolean newUser = (user == null);
        if (!newUser) {
            if (!SCryptUtil.check(userId, user.getPassword())) {
                // This means someone is trying to login with their facebook account, but they already have an account via normal email+password auth
                throw new APIException.BadDataException("Email exists, but it's not a Facebook account.", Collections.singletonList(EMAIL));
            }
        } else {
            // User is new, register.
            user = new User(
                    email,
                    null,
                    false,
                    SCryptUtil.scrypt(userId, 16384, 8, 1), // Using userid as password for now
                    new HashMap<String, Boolean>() {{
                        put(EMAIL, true);
                    }});
            user.addToNotificationMedia(new NotificationMedium(
                    NotificationMedium.NotificationMediumType.FACEBOOK,
                    userId,
                    true
            ));
            dataAccessProvider.getDataAccessContext().getUserDAO().insert(user);
        }
        return new ResponseEntity<>(
                new RefreshTokenResponse(
                        (new RefreshToken(user.getIdentifier().toString())).toToken(secret),
                        user.getIdentifier().toString(),
                        linkTo(methodOn(UserEntityController.class).getUser(user.getIdentifier().toString())).toUri().toString(),
                        USER_ROLE,
                        user.getEmailisValidated()
                ),
                newUser ? HttpStatus.CREATED : HttpStatus.OK
        );
    }

    /**
     * @param body Contains the data used to to initiate the auth flow for google.
     * @return A refresh-token that can be used to obtain an access-token.
     */
    @RequestMapping(value = "google", method = RequestMethod.POST)
    public ResponseEntity<RefreshTokenResponse> googleRefreshToken(@Valid @RequestBody OAuthClientBody body) {
        GoogleIdToken.Payload payload;
        try {
            payload = googleLoginProvider.getPerson(body.getRedirectUri(), body.getCode());

        } catch (IOException e) {
            throw new APIException.ServiceNotAvailableException("Error connecting to google");
        }
        assert payload != null; // Exception will be thrown, so the payload should always be defined. Defensive programming and stuff.
        String email = payload.getEmail().toLowerCase();
        String id = payload.getSubject();
        User user = dataAccessProvider.getDataAccessContext().getUserDAO().findByEmail(email);
        boolean newUser = (user == null);
        if (!newUser) {
            if (!SCryptUtil.check(id, user.getPassword())) {
                // This means the email they are trying to use is already in use, but by another login provider
                throw new APIException.BadDataException("Email exists, but it's not a Google account.", Collections.singletonList(EMAIL));
            }
        } else {
            // User is new
            user = new User(
                    email,
                    null,
                    false,
                    SCryptUtil.scrypt(id, 16384, 8, 1), // Using userid as password for now
                    new HashMap<String, Boolean>() {{
                        put(EMAIL, true);
                    }});
            dataAccessProvider.getDataAccessContext().getUserDAO().insert(user);
        }
        return new ResponseEntity<>(
                new RefreshTokenResponse(
                        (new RefreshToken(user.getIdentifier().toString())).toToken(secret),
                        user.getIdentifier().toString(),
                        linkTo(methodOn(UserEntityController.class).getUser(user.getIdentifier().toString())).toUri().toString(),
                        USER_ROLE,
                        user.getEmailisValidated()
                ),
                newUser ? HttpStatus.CREATED : HttpStatus.OK
        );
    }

    /**
     * Simple class representing the request body when logging in with email and password
     */
    private static class EmailPasswordBody {
        private String email;
        private String password;

        @JsonCreator
        public EmailPasswordBody(@JsonProperty(required = true, value = EMAIL) String email, @JsonProperty(required = true, value = PASSWORD) String password) {
            this.email = email.toLowerCase();
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * Class for returning a refresh token to the client.
     * This gets JSON-ized by jackson when returning.
     */
    private static class RefreshTokenResponse {
        @JsonProperty
        private final String token;
        @JsonProperty
        private final String userId;
        @JsonProperty
        private final String userUrl;
        @JsonProperty
        private final String role;
        @JsonProperty
        private final boolean emailVerified;

        public RefreshTokenResponse(String token, String userId, String userUrl, String role, boolean emailVerified) {
            this.token = token;
            this.userId = userId;
            this.userUrl = userUrl;
            this.role = role;
            this.emailVerified = emailVerified;
        }
    }

    /**
     * Simple class representing the request body when logging in with facebook
     */
    private static class OAuthClientBody {
        @JsonProperty(value = "clientId", required = true)
        private String clientId;
        @JsonProperty(required = true)
        private String code;
        @JsonProperty(value = "redirectUri", required = true)
        private String redirectUri;

        public String getCode() {
            return code;
        }

        public String getRedirectUri() {
            return redirectUri;
        }
    }
}
