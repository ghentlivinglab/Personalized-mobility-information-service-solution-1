package be.ugent.vopro5.backend.businesslayer.businessentities.validators;

/**
 * Created by evert on 18/03/16.
 */
public class ValidationException extends javax.validation.ValidationException {
    /**
     * Create a new ValidationException
     * @param s the error message
     */
    public ValidationException(String s) {
        super(s);
    }
}
