package be.ugent.vopro5.backend.businesslayer.businessworkflow.login;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * Created by thibault on 5/8/16.
 */
public class GoogleLoginProvider {
    @Value("${google.appid}")
    private String appID;

    @Value("${google.appsecret}")
    private String appSecret;

    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * @param redirectUri
     * @param code
     * @return The google user.
     * @throws IOException
     */
    public GoogleIdToken.Payload getPerson(String redirectUri, String code) throws IOException {
        // Turn the authorization code into a refresh token
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY, appID, appSecret, code, redirectUri).execute();
        // Parse the oAuth2 token
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        // Return the payload
        return idToken.getPayload();
    }
}
