package com.fisherman.companion.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.SessionDto;
import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.persistence.SessionRepository;
import com.fisherman.companion.persistence.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${token.expiration.time:86400}")
    private Integer maxAge;

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final TokenGenerateService tokenGenerateService;
    private final HashService hashService;

    @Override
    public ResponseEntity<String> login(final LoginRequest loginRequest, HttpServletResponse response) {
        final LocalDateTime currentTime = LocalDateTime.now();
        final Integer expirationTime = getExpirationTimeInHours(maxAge);
        final String hashedPassword = hashService.hash(loginRequest.password());

        final Long userId = userRepository.loginUser(loginRequest.username(), hashedPassword);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
        }

        final String token = tokenGenerateService.generateToken(loginRequest.username(), hashedPassword, maxAge);

        final SessionDto session = new SessionDto();
        session.setUserId(userId);
        session.setToken(token);
        session.setCreationTime(currentTime);
        session.setExpirationTime(currentTime.plusHours(expirationTime));
        sessionRepository.saveSession(session);

        final Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged in successfully");
    }

    @Override
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        final List<Cookie> cookieList = Optional.ofNullable(request.getCookies())
                                                .map(Arrays::asList)
                                                .orElse(List.of());

        final Optional<Cookie> cookie = cookieList.stream()
                                  .filter(cookies -> cookies.getName().equals("token"))
                                  .findFirst();

        return cookie.map(c -> checkTokenToDeleteSession(c, response))
                     .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided"));
    }

    private ResponseEntity<String> checkTokenToDeleteSession(final Cookie cookie, final HttpServletResponse response) {
        final String token = cookie.getValue();

        if (tokenGenerateService.verifyToken(token)) {
            sessionRepository.deleteSessionByToken(token);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.ok("Logged out successfully");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is unauthorized");
    }

    private Integer getExpirationTimeInHours(Integer maxAge) {
        return maxAge / 3600;
    }
}
