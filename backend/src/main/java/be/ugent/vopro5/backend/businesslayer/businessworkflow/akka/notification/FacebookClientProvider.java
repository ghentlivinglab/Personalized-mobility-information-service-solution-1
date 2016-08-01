package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import org.springframework.beans.factory.annotation.Value;

public class FacebookClientProvider {

    @Value("${facebook.appid}")
    private String clientID;

    @Value("${facebook.appsecret}")
    private String clientSecret;

    /**
     * @return Return a facebookClient that can be used to notify a user
     */
    public FacebookClient getFacebookClient() {
        FacebookClient.AccessToken appAccessToken = new DefaultFacebookClient(Version.VERSION_2_5)
                .obtainAppAccessToken(clientID, clientSecret);
        return new DefaultFacebookClient(
                appAccessToken.getAccessToken(),
                Version.VERSION_2_5
        );
    }
}
