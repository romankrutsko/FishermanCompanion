package com.fisherman.companion.service;

import org.springframework.http.ResponseEntity;

import com.fisherman.companion.dto.request.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    ResponseEntity<String> login(final LoginRequest loginRequest, HttpServletResponse response);

    ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response);
}
