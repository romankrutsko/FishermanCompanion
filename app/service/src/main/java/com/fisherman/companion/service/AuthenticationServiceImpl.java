package com.fisherman.companion.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private final HashService hashService;

    @Override
    public ResponseEntity<String> login(final LoginRequest loginRequest, HttpServletResponse response) {
        final String hashedPassword = hashService.hash(loginRequest.password());

        final Long userId = userRepository.loginUser(loginRequest.username(), hashedPassword);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseStatus.WRONG_CREDENTIALS.getCode());
        }

        final UserDto userDto = userRepository.findUserById(userId);

        cookieService.updateCookies(userDto, response);

        return ResponseEntity.ok(ResponseStatus.LOGGED_IN_SUCCESSFULLY.getCode());
    }

    @Override
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if (cookieService.isNotAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.UNAUTHORIZED.getCode());
        }

        cookieService.deleteAllCookies(request, response);

        return ResponseEntity.ok(ResponseStatus.LOGGED_OUT_SUCCESSFULLY.getCode());
    }
}
