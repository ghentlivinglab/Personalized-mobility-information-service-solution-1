package be.ugent.vopro5.backend.businesslayer.businessworkflow;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;

/**
 * Created by anton on 4/30/16.
 */
public interface GeneralUserVerifier {

    public void sendEmailVerification(User user);

    public boolean verifyEmailPin(User user, String pinAttempt);
}
