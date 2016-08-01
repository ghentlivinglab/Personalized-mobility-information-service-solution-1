package be.ugent.vopro5.backend.businesslayer.businessentities.validators;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing all validators
 */
public class Validators {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * This method checks whether a stirng is not blank
     * @param s: string to validate
     * @param message: errormessagae when string is null or blank
     * @throws ValidationException when string is null or blank
     */
    public static void notBlank(String s, String message) throws ValidationException {
        if (s == null) {
            throw new ValidationException(message);
        }
        if (s.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Checks whether the string is of the correct length
     * @param s: the string to check
     * @param length: the length to match
     * @param message: error message
     * @throws ValidationException when length isn't correct
     */
    public static void length(String s, int length, String message) throws ValidationException {
        if (s == null) {
            throw new ValidationException(message);
        }
        if (s.length() != length) {
            throw new ValidationException(message);
        }
    }

    /**
     * Checks whether a boolean array has the correct length
     * @param bools: the array to check
     * @param length: the length the array must have
     * @param message: error message
     * @throws ValidationException when the array doesn't have the correct length
     */
    public static void length(boolean[] bools, int length, String message) throws ValidationException {
        if (bools == null) {
            throw new ValidationException(message);
        }
        if (bools.length != length) {
            throw new ValidationException(message);
        }
    }

    /**
     * checks whether a floating point number is positive
     * @param f: the number to check
     * @param message: error messagae
     * @throws ValidationException when the number is 0 or negative
     */
    public static void positive(float f, String message) throws ValidationException {
        if (f < 0.0f) {
            throw new ValidationException(message);
        }
    }

    /**
     * checks whether an integer is positive
     * @param i: the integer to check
     * @param message: error messagae
     * @throws ValidationException when the number is 0 or negative
     */
    public static void positive(int i, String message) throws ValidationException {
        if (i < 0) {
            throw new ValidationException(message);
        }
    }

    /**
     * cheks whether an object is null
     * @param o: the object to check
     * @param message: error message
     * @throws ValidationException when the opbject is null
     */
    public static void notNull(Object o, String message) throws ValidationException {
        if (o == null) {
            throw new ValidationException(message);
        }
    }

    /**
     * checks whether a collectino is null
     * @param c: the collection to check
     * @param message: error message
     * @throws ValidationException whe the collectino is null or empty
     */
    public static void notEmpty(Collection c, String message) throws ValidationException {
        if (c == null) {
            throw new ValidationException(message);
        }
        if (c.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Checks wheather a comparable lies in between to values
     * @param actual: the actual value to check
     * @param min: the minimal allowed value
     * @param max: the maximal allowed value
     * @param message: error message
     * @param <T>: the type (extending Comparable)
     * @throws ValidationException when the actual value lies outside of the min and max
     */
    public static <T extends Comparable<T>> void verifyComparable(T actual, T min, T max, String message) throws ValidationException {
        if (actual.compareTo(min) < 0 || actual.compareTo(max) > 0) {
            throw new ValidationException(message);
        }
    }

    /**
     * Checks whether an email is vallid
     * @param email: the email to check
     * @param message: error message
     * @throws ValidationException when the email is invalid
     */
    public static void email(String email, String message) throws ValidationException {
        notBlank(email, message);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        if (!matcher.find()) {
            throw new ValidationException(message);
        }
    }

    /**
     * checks if a boolean (expression) is true
     * @param b: the boolean (expression)
     * @param message: error message
     * @throws ValidationException when the boolean is false
     */
    public static void validationAssert(boolean b, String message) throws ValidationException {
        if (!b) {
            throw new ValidationException(message);
        }
    }
}
