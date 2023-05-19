package com.fisherman.companion.service;

import com.fisherman.companion.dto.SignTokenParams;
import com.fisherman.companion.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {
    boolean isTokenValid(final String token);

    UserDto verifyAuthentication(HttpServletRequest request);

    String generateToken(SignTokenParams params, HttpServletResponse response);

}
