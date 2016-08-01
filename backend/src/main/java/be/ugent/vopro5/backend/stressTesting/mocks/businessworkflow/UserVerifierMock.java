package be.ugent.vopro5.backend.stressTesting.mocks.businessworkflow;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.GeneralUserVerifier;

/**
 * Created by anton on 4/30/16.
 */
public class UserVerifierMock implements GeneralUserVerifier{

    @Override
    public void sendEmailVerification(User user) {
        //do nothing
    }

    @Override
    public boolean verifyEmailPin(User user, String pinAttempt){
        return true;
    }
}
