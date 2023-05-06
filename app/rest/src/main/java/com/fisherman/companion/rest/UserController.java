package com.fisherman.companion.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdatePasswordRequest;
import com.fisherman.companion.dto.request.UpdateUsernameRequest;
import com.fisherman.companion.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    String createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/change/password")
    String updatePassword(HttpServletRequest request, @RequestBody UpdatePasswordRequest passwordRequest, HttpServletResponse response) {
        return userService.updateUserPassword(request, passwordRequest, response);
    }

    @PostMapping("/change/username")
    String updateUsername(HttpServletRequest request, @RequestBody UpdateUsernameRequest usernameRequest, HttpServletResponse response) {
        return userService.updateUsername(request, usernameRequest, response);
    }

    @PostMapping("/delete")
    String deleteUser(HttpServletRequest request, HttpServletResponse response) {
        return userService.deleteUser(request, response);
    }
}
