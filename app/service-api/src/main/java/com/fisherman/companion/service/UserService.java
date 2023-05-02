package com.fisherman.companion.service;

import org.springframework.http.ResponseEntity;

import com.fisherman.companion.dto.request.CreateUserRequest;

public interface UserService {
    ResponseEntity<String> createUser(CreateUserRequest createUserRequest);
}
