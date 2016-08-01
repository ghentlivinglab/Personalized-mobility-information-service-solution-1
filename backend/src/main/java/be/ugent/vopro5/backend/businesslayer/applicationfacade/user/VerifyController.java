package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.GeneralUserVerifier;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.UserVerifier;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Created by thibault on 4/7/16.
 */
@RestController
@RequestMapping("/user/{id}/verify")
public class VerifyController {
    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private GeneralUserVerifier verifier;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * Verifies the pin that was sent to a user's notification-medium. Ex. to verify the email-address of a user, an email
     * containing a pin will be sent to that email-address. If the user is the truthful owner of that email-address then
     * he/she can access the pin and request to verify it.
     * @param id The ID of the user.
     * @param body Contains the pin that was sent to the user's notification-medium. Currently only email-addresses are supported.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public void verify(@PathVariable String id, @Valid @RequestBody VerifyInputBody body) {
        concurrencyController.enter(id);
        try {
            User user = dataAccessProvider.getDataAccessContext().getUserDAO().find(id);
            if (user == null) {
                throw new APIException.DataNotFoundException("User does not exist", Collections.emptyList());
            }
            if (body.emailVerificationPin != null) {
                if (verifier.verifyEmailPin(user, body.emailVerificationPin)) {
                    user.setEmailisValidated();
                    dataAccessProvider.getDataAccessContext().getUserDAO().update(user);
                } else {
                    throw new APIException.BadDataException("Email verification is wrong", Collections.singletonList("email_verification_pin"));
                }
            }
            if (body.cellNumberVerificationPin != null) {
                // We don't do anything here, because we don't send verification PINs to the cell number (yet)
                // We don't send an SMS for the simple reason we don't have any money to spend
            }
        } finally {
            concurrencyController.leave(id);
        }
    }

    private static class VerifyInputBody {
        /**
         * The email verification pin code
         */
        @JsonProperty(value = "email_verification_pin")
        public String emailVerificationPin;
        /**
         * The cell number verification pin
         */
        @JsonProperty(value = "cell_number_verification_pin")
        public String cellNumberVerificationPin;

    }
}
