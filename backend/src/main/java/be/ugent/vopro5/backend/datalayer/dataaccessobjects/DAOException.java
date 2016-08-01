package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import java.io.IOException;

/**
 * Created by anton on 09.03.16.
 */
public class DAOException extends RuntimeException {

    /**
     * Create a new DAOException
     * @param message error message
     * @param e the IO exception
     */
    public DAOException(String message, IOException e) {
        super(message, e);
    }

    /**
     * Exception that can be thrown when an error occurred while reading from the database
     */
    public static class DAOReadException extends DAOException {
        /**
         * Create a new DAOReadException
         * @param message error message
         * @param e
         */
        public DAOReadException(String message, IOException e) {
            super(message, e);
        }
    }

    /**
     * Exception that can be thrown when an error occurred while writing from the database
     */
    public static class DAOWriteException extends DAOException {
        /**
         * Create a new DAOWriteException
         * @param message error message
         * @param e
         */
        public DAOWriteException(String message, IOException e) {
            super(message, e);
        }
    }
}
