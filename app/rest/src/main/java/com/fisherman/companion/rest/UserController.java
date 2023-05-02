package com.fisherman.companion.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }
}
