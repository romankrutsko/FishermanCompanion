package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    String login(final LoginRequest loginRequest, HttpServletResponse response);

    String logout(HttpServletRequest request, HttpServletResponse response);
}
