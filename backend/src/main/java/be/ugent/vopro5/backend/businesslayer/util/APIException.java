package be.ugent.vopro5.backend.businesslayer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thibault on 3/1/16.
 */
public abstract class APIException extends RuntimeException {
    private final List<String> fields;

    /**
     * @param message The message that contains additional information.
     * @param fields The fields which caused the exception.
     */
    public APIException(String message, List<String> fields) {
        super(message);
        if (fields == null) {
            this.fields = new ArrayList<>();
        } else {
            this.fields = fields;
        }
    }

    /**
     * @return The list that contains the field that caused the error.
     */
    public List<String> getFields() {
        return fields;
    }

    /**
     * Exception that can be thrown on a data conflict
     */
    public static class DataConflictException extends APIException {
        /**
         * Create a new DataConflictException
         * @param message The message that contains additional information.
         * @param fields list of the conflicting  fields
         */
        public DataConflictException(String message, List<String> fields) {
            super(message, fields);
        }
    }

    /**
     * Exception that can be thrown when data can not be found
     */
    public static class DataNotFoundException extends APIException {
        /**
         * Create a new DataNotFoundException
         * @param message The message that contains additional information.
         * @param fields list of fields that can not be found
         */
        public DataNotFoundException(String message, List<String> fields) {
            super(message, fields);
        }
    }

    /**
     * Exception that can be thrown when receiving bad data
     */
    public static class BadDataException extends APIException {
        /**
         * create a new BadDataException
         * @param message The message that contains additional information.
         * @param fields list of fields that are bad
         */
        public BadDataException(String message, List<String> fields) {
            super(message, fields);
        }
    }

    /**
     * Exception that can be thrown when an action is forbidden
     */
    public static class ForbiddenException extends APIException {
        /**
         * Create a new ForbiddenException
         * @param message The message that contains additional information.
         */
        public ForbiddenException(String message) { super(message, null); }
    }

    /**
     * Exception that can be thrown when an action is unauthorized
     */
    public static class UnauthorizedException extends APIException {
        /**
         * Create a new UnauthorizedException
         * @param message The message that contains additional information.
         * @param fields
         */
        public UnauthorizedException(String message, List<String> fields) {
            super(message, fields);
        }
    }

    /**
     * Exception that can be thrown when an internal error occured
     */
    public static class InternalErrorException extends APIException {

        /**
         * @param message The message that contains additional information.
         */
        public InternalErrorException(String message) {
            super(message, Collections.emptyList());
        }
    }

    /**
     * Exception that can be thrown when a service is unavailable
     */
    public static class ServiceNotAvailableException extends APIException {

        /**
         * @param message The message that contains additional information.
         */
        public ServiceNotAvailableException(String message) {
            super(message, Collections.emptyList());
        }
    }
}
