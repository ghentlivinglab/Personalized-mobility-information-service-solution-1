package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.UUID;

/**
 * Created by thibault on 4/4/16.
 */
public class Operator extends Person {
    /**
     * Create a new operator. An operator is a person granted privileges by the admin to lookup, create and modify
     * events manually.
     * @param identifier
     * @param email
     * @param password
     * @throws ValidationException
     */
    public Operator(UUID identifier, NotificationMedium email, String password) throws ValidationException {
        super(identifier, email, password);
    }

    /**
     * create a new operator
     * This constructor generates a random UUID for this operator
     * @param email
     * @param password
     * @throws ValidationException
     */
    public Operator(String email, String password) throws ValidationException {
        super(UUID.randomUUID(), new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, email, true), password);
    }

    /**
     * creates a copy of another operator
     * @param other: the operator from which the properties should be copied
     */
    public void transferProperties(Operator other) {
        setEmail(other.getEmail());
        setPassword(other.getPassword());
    }
}
