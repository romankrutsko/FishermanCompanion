package com.fisherman.companion.service;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.SignTokenParams;
import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.dto.response.LoginResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.dto.response.UserResponse;
import com.fisherman.companion.service.exception.RequestException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final TokenService tokenService;
    private final HashService hashService;

    @Override
    public LoginResponse login(final LoginRequest loginRequest, HttpServletResponse response) {
        final String hashedPassword = hashService.hash(loginRequest.password());

        final Long userId = userService.loginUser(loginRequest.username(), hashedPassword);

        if (userId == null) {
            throw new RequestException(ResponseStatus.WRONG_CREDENTIALS.getCode());
        }

        final UserResponse user = userService.findUserById(userId);

        final SignTokenParams params = SignTokenParams.builder()
                                                      .id(userId)
                                                      .username(user.getUsername())
                                                      .role(user.getRole())
                                                      .build();

        final String token = tokenService.generateToken(params, response);

        return LoginResponse.builder()
                            .user(user)
                            .token(token)
                            .build();
    }

    @Override
    public String logout(HttpServletRequest request) {
        tokenService.verifyAuthentication(request);

        return ResponseStatus.LOGGED_OUT_SUCCESSFULLY.getCode();
    }
}
