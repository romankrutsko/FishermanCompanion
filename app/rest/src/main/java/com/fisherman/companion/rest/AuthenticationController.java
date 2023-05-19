package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.dto.response.LoginResponse;
import com.fisherman.companion.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authenticationService.login(request, response);
    }

    @PostMapping("/logout")
    String logout(HttpServletRequest request) {
        return authenticationService.logout(request);
    }
}
