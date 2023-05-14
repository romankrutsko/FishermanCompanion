package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdatePasswordRequest;
import com.fisherman.companion.dto.request.UpdateUsernameRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    Long createUser(CreateUserRequest createUserRequest);

    String updateUserPassword(HttpServletRequest request, UpdatePasswordRequest passwordRequest);

    String updateUsername(HttpServletRequest request, UpdateUsernameRequest usernameRequest, HttpServletResponse response);

    String deleteUser(HttpServletRequest request, HttpServletResponse response, Long userId);
}
