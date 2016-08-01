package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thibault on 3/1/16.
 */
@ControllerAdvice
@ResponseBody
public class GlobalErrorHandler {

    /**
     * @param ex Thrown MethodArgumentNotValidException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Error handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> fields = new ArrayList<>();
        FieldError error = ex.getBindingResult().getFieldError();
        fields.add(error.getField());
        return new Error(HttpStatus.BAD_REQUEST.value(), error.getDefaultMessage(), fields);
    }

    /**
     * @param ex Thrown ValidationException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Error handleValidationException(ValidationException ex) {
        return new Error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Collections.emptyList());
    }

    /**
     * @param ex Thrown ValidationException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Error handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return new Error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Collections.emptyList());
    }

    /**
     * @param ex Thrown DataConflictException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(APIException.DataConflictException.class)
    public Error handleDataConflictException(APIException.DataConflictException ex) {
        return new Error(HttpStatus.CONFLICT.value(), ex.getMessage(), ex.getFields());
    }

    /**
     * @param ex Thrown DataNotFoundException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(APIException.DataNotFoundException.class)
    public Error handleDataNotFoundException(APIException.DataNotFoundException ex) {
        return new Error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ex.getFields());
    }

    /**
     * @param ex Thrown BadDataException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(APIException.BadDataException.class)
    @ResponseBody
    public Error handleBadDataException(APIException.BadDataException ex) {
        return new Error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getFields());
    }

    /**
     * @param ex Thrown ServletRequestBindingException.
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public Error handleServletRequestBindingException(ServletRequestBindingException ex) {
        return new Error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Collections.emptyList());
    }

    /**
     * @param ex Thrown ForbiddenException
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(APIException.ForbiddenException.class)
    @ResponseBody
    public Error handleForbiddenException(APIException.ForbiddenException ex) {
        return new Error(HttpStatus.FORBIDDEN.value(), ex.getMessage(), ex.getFields());
    }

    /**
     * @param ex Thrown UnauthorizedException
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(APIException.UnauthorizedException.class)
    @ResponseBody
    public Error handleUnauthorizedException(APIException.UnauthorizedException ex) {
        return new Error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), ex.getFields());
    }

    /**
     * @param ex Thrown InternalErrorException
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(APIException.InternalErrorException.class)
    @ResponseBody
    public Error handleInternalErrorException(APIException.InternalErrorException ex) {
        ex.printStackTrace();
        return new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "The server encountered an internal error: " + ex.getMessage(), Collections.emptyList());
    }

    /**
     * @param ex Thrown Exception
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Error handleOtherException(Exception ex) {
        ex.printStackTrace();
        return new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "The server encountered an internal error.", Collections.emptyList());
    }

    /**
     * @param ex Thrown ServiceNotAvailableException
     * @return The error object containing info about the thrown exception.
     */
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(APIException.ServiceNotAvailableException.class)
    @ResponseBody
    public Error handleServiceNotAvailableException(APIException.ServiceNotAvailableException ex) {
        return new Error(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage(), Collections.emptyList());
    }

}
