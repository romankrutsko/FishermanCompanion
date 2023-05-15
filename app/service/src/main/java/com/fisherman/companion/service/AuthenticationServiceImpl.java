package com.fisherman.companion.service;

import org.springframework.stereotype.Service;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.LoginRequest;
import com.fisherman.companion.dto.response.LoginResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.UserRepository;
import com.fisherman.companion.service.exception.RequestException;

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
    public LoginResponse login(final LoginRequest loginRequest, HttpServletResponse response) {
        final String hashedPassword = hashService.hash(loginRequest.password());

        final Long userId = userRepository.loginUser(loginRequest.username(), hashedPassword);

        if (userId == null) {
            throw new RequestException(ResponseStatus.WRONG_CREDENTIALS.getCode());
        }

        final UserDto userDto = userRepository.findUserById(userId);

        final String cookies = cookieService.updateCookies(userDto, response);

        return LoginResponse.builder()
                            .id(userDto.getId())
                            .token(cookies)
                            .role(userDto.getRole())
                            .build();
    }

    @Override
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.verifyAuthentication(request);

        cookieService.deleteAllCookies(request, response);

        return ResponseStatus.LOGGED_OUT_SUCCESSFULLY.getCode();
    }
}
