package com.fisherman.companion.service;

import org.springframework.http.ResponseEntity;

import com.fisherman.companion.dto.request.LoginRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    ResponseEntity<String> login(LoginRequest loginRequest, HttpServletResponse response);
}
