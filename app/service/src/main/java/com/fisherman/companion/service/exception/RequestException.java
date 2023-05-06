package com.fisherman.companion.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
@AllArgsConstructor
public class RequestException extends RuntimeException {
    private final String message;
}
