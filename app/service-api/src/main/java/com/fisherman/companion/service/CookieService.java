package com.fisherman.companion.service;

import com.fisherman.companion.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    boolean isTokenValid(final String token);

    UserDto verifyAuthentication(HttpServletRequest request);

    String getToken(HttpServletRequest request);

    String updateCookies(UserDto userDto, HttpServletResponse response);

    void deleteAllCookies(HttpServletRequest request, HttpServletResponse response);
}
