package be.ugent.vopro5.backend.businesslayer.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 28/02/16.
 */
public class Error {
    private final int status;
    private final String message;
    private final List<String> fields;

    /**
     * @param status the HTTP status code.
     * @param message the message with additional information.
     * @param fields  the fields that cause the error.
     */
    public Error(int status, String message, List<String> fields) {
        this.status = status;
        this.message = message;
        if (fields == null) {
            this.fields = new ArrayList<>();
        } else {
            this.fields = fields;
        }
    }

    /**
     * @return The HTTP status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return The message with additional information.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return The list of fields that caused the error.
     */
    public List<String> getFields() {
        return fields;
    }
}
