package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.email;
import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notBlank;
import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notNull;

/**
 * Created by thibault on 4/4/16.
 */
public class Person extends Identifiable {
    protected NotificationMedium email;
    private String password;

    /**
     * create a new person
     * @param identifier
     * @param email
     * @param password
     * @throws ValidationException
     */
    public Person(UUID identifier, NotificationMedium email, String password) throws ValidationException {
        super(identifier);
        notNull(email, "Email may not be null");
        boolean validated = email.isValidated();
        email.setValue(email.getValue().toLowerCase());
        if(validated) {
            email.validate();
        }
        email(email.getValue(), "Email must be valid");
        this.email = email;
        notBlank(password, "Password can not be blank");
        this.password = password;
    }

    /**
     * @return The *HASHED* version of this user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password Set the *HASHED* version of this user's password.
     */
    public void setPassword(String password) {
        notBlank(password, "Password may not be blank");
        this.password = password;
    }

    public String getEmail() {
        return email.getValue();
    }

    public void setEmail(String email) throws ValidationException {
        notBlank(email, "Email cannot be blank");
        email(email, "Email is not valid");
        this.email = new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, email, false);
    }

    /**
     * Validate the email of this person
     */
    public void setEmailisValidated() {
        this.email.validate();
    }

    /**
     * @return True if the email has been validated, else return false.
     */
    public boolean getEmailisValidated() {
        return email.isValidated();
    }

    /**
     * @return The verification code sent to this person's email address.
     */
    public String getEmailVerification() {
        return this.email.getPin();
    }
}
