package com.fisherman.companion.service;

import com.fisherman.companion.dto.SignTokenParams;
import com.fisherman.companion.dto.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {
    boolean isTokenValid(final String token);

    User verifyAuthentication(HttpServletRequest request);

    String generateToken(SignTokenParams params, HttpServletResponse response);

}
