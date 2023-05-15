package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.dto.response.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    LoginResponse login(final LoginRequest loginRequest, HttpServletResponse response);

    String logout(HttpServletRequest request, HttpServletResponse response);
}
