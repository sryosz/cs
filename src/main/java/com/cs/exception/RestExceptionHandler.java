package com.cs.exception;

import com.cs.exception.user.UserAlreadyExistsException;
import com.cs.exception.user.UserErrorResponse;
import com.cs.exception.user.UserNotFoundException;
import com.cs.exception.user.UserValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<UserErrorResponse> handleValidationException(Exception exc){

        UserErrorResponse error = new UserErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exc.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponse> handleNotFoundException(Exception exc){

        UserErrorResponse error = new UserErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exc.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponse> handleAlreadyExistsException(Exception exc){

        UserErrorResponse error = new UserErrorResponse(
                HttpStatus.CONFLICT.value(),
                exc.getMessage(),
                LocalDateTime.now().toString()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

}
