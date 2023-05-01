package com.fisherman.companion.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.SessionDto;
import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.persistence.SessionRepository;
import com.fisherman.companion.persistence.UserRepository;
import com.fisherman.companion.service.utils.PasswordHashService;
import com.fisherman.companion.service.utils.UuidUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${uuid.expiration.time:86400}")
    private Integer maxAge;

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletResponse response) {

        LocalDateTime currentTime = LocalDateTime.now();
        Integer expirationTime = getExpirationTimeInHours(maxAge);
        String hashedPassword = PasswordHashService.hash(loginRequest.getPassword());

        Long userId = userRepository.loginUser(loginRequest.username(), hashedPassword);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        SessionDto session = new SessionDto();
        session.setUserId(userId);
        session.setToken(UuidUtils.generateUuid());
        session.setCreationTime(currentTime);
        session.setExpirationTime(currentTime.plusHours(expirationTime));
        sessionRepository.saveSession(session);

        Cookie cookie = new Cookie("token", session.getToken());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged in successfully");
    }

    private Integer getExpirationTimeInHours(Integer maxAge) {
        return maxAge / 3600;
    }
}
