package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.Error;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by evert on 4/13/16.
 */
public class GlobalErrorHandlerTest {

    private GlobalErrorHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new GlobalErrorHandler();
    }

    @Test
    public void handleMethodArgumentNotValidException() throws Exception {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = Mockito.mock(FieldError.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        when(fieldError.getField()).thenReturn("Method argument not valid");
        when(fieldError.getDefaultMessage()).thenReturn("Default message");
        Error error = handler.handleMethodArgumentNotValidException(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Default message", error.getMessage());
        assertEquals(Collections.singletonList("Method argument not valid"), error.getFields());
    }

    @Test
    public void handleValidationException() throws Exception {
        Error error = handler.handleValidationException(new ValidationException("Validation failed"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Validation failed", error.getMessage());
        assertEquals(Collections.emptyList(), error.getFields());
    }

    @Test
    public void handleMessageNotReadable() throws Exception {
        Error error = handler.handleMessageNotReadable(new HttpMessageNotReadableException("Message not readable"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Message not readable", error.getMessage());
        assertEquals(Collections.emptyList(), error.getFields());
    }

    @Test
    public void handleDataConflictException() throws Exception {
        Error error = handler.handleDataConflictException(new APIException.DataConflictException("Data conflict", Collections.singletonList("field")));
        assertEquals(HttpStatus.CONFLICT.value(), error.getStatus());
        assertEquals("Data conflict", error.getMessage());
        assertEquals(Collections.singletonList("field"), error.getFields());
    }

    @Test
    public void handleDataNotFoundException() throws Exception {
        Error error = handler.handleDataNotFoundException(new APIException.DataNotFoundException("Data not found", Collections.singletonList("field")));
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Data not found", error.getMessage());
        assertEquals(Collections.singletonList("field"), error.getFields());
    }

    @Test
    public void handleBadDataException() throws Exception {
        Error error = handler.handleBadDataException(new APIException.BadDataException("Bad data", Collections.singletonList("field")));
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Bad data", error.getMessage());
        assertEquals(Collections.singletonList("field"), error.getFields());
    }

    @Test
    public void handleServletRequestBindingException() throws Exception {
        Error error = handler.handleServletRequestBindingException(new ServletRequestBindingException("Servlet request binding failed"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Servlet request binding failed", error.getMessage());
        assertEquals(Collections.emptyList(), error.getFields());
    }

    @Test
    public void handleForbiddenException() throws Exception {
        Error error = handler.handleForbiddenException(new APIException.ForbiddenException("Forbidden"));
        assertEquals(HttpStatus.FORBIDDEN.value(), error.getStatus());
        assertEquals("Forbidden", error.getMessage());
        assertEquals(Collections.emptyList(), error.getFields());
    }

    @Test
    public void handleUnauthorizedException() throws Exception {
        Error error = handler.handleUnauthorizedException(new APIException.UnauthorizedException("Unauthorized", Collections.singletonList("field")));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        assertEquals("Unauthorized", error.getMessage());
        assertEquals(Collections.singletonList("field"), error.getFields());
    }

    @Test
    public void handleInternalErrorException() throws Exception {
        Error error = handler.handleInternalErrorException(new APIException.InternalErrorException("internal error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        assertEquals("The server encountered an internal error: internal error", error.getMessage());
        assertEquals(Collections.emptyList(), error.getFields());
    }

    @Test
    public void handleOtherException() throws Exception {
        Error error = handler.handleOtherException(new Exception());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        assertEquals("The server encountered an internal error.", error.getMessage());
        assertEquals(Collections.emptyList(), error.getFields());
    }

}