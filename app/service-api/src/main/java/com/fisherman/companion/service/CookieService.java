package com.fisherman.companion.service;

import com.fisherman.companion.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    boolean isTokenValid(final String token);

    boolean isNotAuthenticated(HttpServletRequest request);

    String getToken(HttpServletRequest request);

    UserDto getUserFromCookies(HttpServletRequest request);

    void updateCookies(UserDto userDto, HttpServletResponse response);

    void deleteAllCookies(HttpServletRequest request, HttpServletResponse response);
}
