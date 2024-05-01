package com.baeldung.webrtc.controller;

import com.baeldung.webrtc.dao.response.MyErrorResponse;
import com.baeldung.webrtc.exceptions.CustomAuthenticationException;
import com.baeldung.webrtc.exceptions.CustomDataNotFoundException;
import com.baeldung.webrtc.exceptions.CustomErrorException;
import com.baeldung.webrtc.exceptions.CustomParameterConstraintException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.view.RedirectView;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class CustomControllerAdvice {
    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<MyErrorResponse> handleCustomErrorExceptions(Exception e) {
        // casting the generic Exception e to CustomErrorException
        CustomErrorException customErrorException = (CustomErrorException) e;

        HttpStatus status = customErrorException.getStatus();

        // converting the stack trace to String
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        customErrorException.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return new ResponseEntity<>(
                new MyErrorResponse(
                        status,
                        customErrorException.getMessage(),
                        stackTrace,
                        customErrorException.getData()
                ),
                status
        );
    }


    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<MyErrorResponse> handleCustomAuthenticationExceptions(Exception e) {
        HttpStatus status = HttpStatus.FORBIDDEN; // 403

        // converting the stack trace to String
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return new ResponseEntity<>(new MyErrorResponse(status, e.getMessage(), stackTrace), status);
    }

    @ExceptionHandler(CustomDataNotFoundException.class)
    public ResponseEntity<MyErrorResponse> handleCustomDataNotFoundExceptions(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        // converting the stack trace to String
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return new ResponseEntity<>(new MyErrorResponse(status, e.getMessage(), stackTrace), status);
    }

    @ExceptionHandler(CustomParameterConstraintException.class)
    public ResponseEntity<MyErrorResponse> handleCustomParameterConstraintExceptions(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        return new ResponseEntity<>(new MyErrorResponse(status, e.getMessage()), status);
    }

//    @ExceptionHandler(NullPointerException.class) // exception handled
//    public ResponseEntity<MyErrorResponse> handleNullPointerExceptions(Exception e) {
//        HttpStatus status = HttpStatus.NOT_FOUND; // 404
//        return new ResponseEntity<>(new MyErrorResponse(status, e.getMessage()), status);
//    }

    @ExceptionHandler(NullPointerException.class) // exception handled
    public RedirectView handleNullPointerExceptions(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        return new RedirectView("/#/error", true);
    }


    // fallback method
    @ExceptionHandler(Exception.class) // exception handled
    public ResponseEntity<MyErrorResponse> handleExceptions(Exception e) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500

        // converting the stack trace to String
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return new ResponseEntity<>(new MyErrorResponse(status, e.getMessage(), stackTrace), status);
    }
}