package com.fisherman.companion.service.exception;

import org.springframework.http.HttpStatus;

public record RequestException(String message, HttpStatus status) {
}
