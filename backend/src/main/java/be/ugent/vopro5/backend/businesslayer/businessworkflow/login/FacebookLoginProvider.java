package be.ugent.vopro5.backend.businesslayer.businessworkflow.login;

import be.ugent.vopro5.backend.businesslayer.util.APIException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;

/**
 * Created by thibault on 4/13/16.
 */
public class FacebookLoginProvider {
    @Value("${facebook.appid}")
    private String facebookAppId;

    @Value("${facebook.appsecret}")
    private String facebookAppSecret;

    /**
     * Get a user from Facebook
     * @param redirectUri
     * @param code
     * @return
     */
    public User getFacebookUserFromCode(String redirectUri, String code) {
        // Get access token from authentication code
        DefaultFacebookClient facebookClient = new DefaultFacebookClient(Version.VERSION_2_5);
        FacebookClient.AccessToken accessToken;
        try {
            accessToken = facebookClient.obtainUserAccessToken(
                    facebookAppId,
                    facebookAppSecret,
                    redirectUri,
                    code
            );
        } catch (FacebookException e) {
            throw new APIException.BadDataException("Unable to get access code from facebook", Collections.singletonList("code"));
        }
        // Get user details with access token
        facebookClient = new DefaultFacebookClient(accessToken.getAccessToken(), Version.VERSION_2_5);
        return facebookClient.fetchObject("me", User.class, Parameter.with("fields", "email"));
    }
}
