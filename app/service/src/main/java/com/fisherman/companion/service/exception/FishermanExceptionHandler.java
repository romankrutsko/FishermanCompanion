package com.fisherman.companion.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class FishermanExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        final RequestException exception = new RequestException(
                ex.getMessage(), HttpStatus.BAD_REQUEST
        );

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUnauthorizedRequestException(UnauthorizedException ex) {
        final RequestException exception = new RequestException(
                ex.getMessage(), HttpStatus.UNAUTHORIZED
        );

        return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
    }
}
