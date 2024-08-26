package org.example.user.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<RestError> handleCustomException(CustomException exception) {
        return new ResponseEntity<>(new RestError(exception.getClass().getSimpleName(), exception.getMessage()), exception.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<RestError> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        StringBuilder sb=new StringBuilder();
        List<FieldError> fieldErrors = exception.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage());
            sb.append("\n");
        }

        return new ResponseEntity<>(new RestError(exception.getClass().getSimpleName(), sb.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RestError> handleMethodSecurityException(AuthorizationDeniedException exception) {
        return new ResponseEntity<>(new RestError(exception.getClass().getSimpleName(), exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<RestError> handleAnyException(RuntimeException exception) {
        return new ResponseEntity<>(new RestError(exception.getClass().getSimpleName(), exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
