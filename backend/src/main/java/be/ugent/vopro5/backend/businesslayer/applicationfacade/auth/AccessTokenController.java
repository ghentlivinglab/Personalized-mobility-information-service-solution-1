package be.ugent.vopro5.backend.businesslayer.applicationfacade.auth;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.user.UserEntityController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.AccessToken;
import be.ugent.vopro5.backend.businesslayer.util.RefreshToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lambdaworks.crypto.SCryptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by thibault on 4/3/16.
 */
@RestController
@RequestMapping("/access_token/")
public class AccessTokenController {

    private static final int ACCESS_TOKEN_VALID_FOR_SECONDS = 30 * 60; // 30 minutes of validity for access tokens, as advised by OAuth2 specs

    @Value("${jwt.secret}")
    private String secret;

    /**
     * @param body The refresh-token used to verify the authorization.
     * @return An access-token is returned when the refresh-token is authorized.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public AccessTokenResponse createAccessToken(@Valid @RequestBody RefreshTokenBody body) {
        RefreshToken refreshToken = RefreshToken.parseToken(secret, body.getRefreshToken());
        if (refreshToken == null) {
            throw new APIException.UnauthorizedException("Refresh Token not valid", Collections.singletonList("refreshToken"));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, ACCESS_TOKEN_VALID_FOR_SECONDS);
        Date exp = calendar.getTime();
        return new AccessTokenResponse(
                (new AccessToken(refreshToken.getSubject(), exp)).toToken(secret),
                exp
        );
    }

    /**
     * Class for returning an access-token to the client.
     */
    private static class AccessTokenResponse {
        @JsonProperty
        private final String token;
        @JsonProperty
        private final Date exp;

        public AccessTokenResponse(String token, Date exp) {
            this.token = token;
            this.exp = exp;
        }
    }

    /**
     * This class represents the body containing the refresh-token that is used to request an access-token.
     */
    private static class RefreshTokenBody {
        @JsonProperty(required = true)
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
