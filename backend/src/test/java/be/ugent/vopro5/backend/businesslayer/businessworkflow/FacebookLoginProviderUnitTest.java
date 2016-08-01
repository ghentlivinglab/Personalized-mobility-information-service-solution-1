package be.ugent.vopro5.backend.businesslayer.businessworkflow;

import be.ugent.vopro5.backend.businesslayer.businessworkflow.login.FacebookLoginProvider;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.types.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by thibault on 4/13/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FacebookLoginProvider.class, DefaultFacebookClient.class})
public class FacebookLoginProviderUnitTest {

    public static final String SOME_ACCESS_TOKEN = "some_access_token";
    public static final String TEST_UGENT_BE = "test@ugent.be";

    @Test
    public void testFacebookLogin() throws Exception {
        DefaultFacebookClient client1 = mock(DefaultFacebookClient.class);
        expectNew(DefaultFacebookClient.class, Version.VERSION_2_5).andReturn(client1);

        FacebookClient.AccessToken accessToken = mock(FacebookClient.AccessToken.class);
        when(accessToken.getAccessToken()).thenReturn(SOME_ACCESS_TOKEN);
        when(client1.obtainUserAccessToken(any(), any(), any(), any())).thenReturn(accessToken);

        DefaultFacebookClient client2 = mock(DefaultFacebookClient.class);
        expectNew(DefaultFacebookClient.class, SOME_ACCESS_TOKEN, Version.VERSION_2_5).andReturn(client2);

        User user = new User();
        user.setEmail(TEST_UGENT_BE);
        when(client2.fetchObject(any(), any(), any())).thenReturn(user);

        FacebookLoginProvider facebookLoginProvider = new FacebookLoginProvider();


        replayAll();

        User returnedUser = facebookLoginProvider.getFacebookUserFromCode("anything", "works");

        assertEquals(user.getEmail(), returnedUser.getEmail());

        verifyAll();
    }

    @Test(expected = APIException.BadDataException.class)
    public void testFacebookLoginFacebookError() throws Exception {
        DefaultFacebookClient client1 = mock(DefaultFacebookClient.class);
        expectNew(DefaultFacebookClient.class, Version.VERSION_2_5).andReturn(client1);

        when(client1.obtainUserAccessToken(any(), any(), any(), any())).thenThrow(new FacebookNetworkException("Internal error at FB", 500));

        FacebookLoginProvider facebookLoginProvider = new FacebookLoginProvider();


        replayAll();
        facebookLoginProvider.getFacebookUserFromCode("anything", "works");
        verifyAll();
    }
}
