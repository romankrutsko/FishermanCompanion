package com.fisherman.companion.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
    private final String message;
}