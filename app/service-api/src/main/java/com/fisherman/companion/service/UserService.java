package com.fisherman.companion.service;

import org.springframework.http.ResponseEntity;

import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdatePasswordRequest;
import com.fisherman.companion.dto.request.UpdateUsernameRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResponseEntity<String> createUser(CreateUserRequest createUserRequest);

    ResponseEntity<String> updateUserPassword(HttpServletRequest request, UpdatePasswordRequest passwordRequest, HttpServletResponse response);

    ResponseEntity<String> updateUsername(HttpServletRequest request, UpdateUsernameRequest usernameRequest, HttpServletResponse response);

    ResponseEntity<String> deleteUser(HttpServletRequest request, HttpServletResponse response);
}
