package com.fisherman.companion.service;

import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.UserRole;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdatePasswordRequest;
import com.fisherman.companion.dto.request.UpdateUsernameRequest;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private final HashService hashService;

    @Override
    public ResponseEntity<String> createUser(final CreateUserRequest createUserRequest) {
        final String username = createUserRequest.username();
        final String email = createUserRequest.email();
        final String hashedPassword = hashService.hash(createUserRequest.password());

        if (userRepository.isUsernameNotUnique(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseStatus.USERNAME_IS_TAKEN.getCode());
        }

        if (userRepository.isEmailNotUnique(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseStatus.EMAIL_IS_TAKEN.getCode());
        }

        return saveUser(createUserRequest, hashedPassword);
    }

    private ResponseEntity<String> saveUser(final CreateUserRequest createUserRequest, final String hashedPassword) {
        final UserDto userDto = new UserDto();
        userDto.setEmail(createUserRequest.email());
        userDto.setPassword(hashedPassword);
        userDto.setUsername(createUserRequest.username());
        userDto.setRole(UserRole.USER.name().toLowerCase());

        userRepository.saveUser(userDto);
        return ResponseEntity.ok(ResponseStatus.USER_CREATED_SUCCESSFULLY.getCode());
    }

    @Override
    public ResponseEntity<String> updateUserPassword(final HttpServletRequest request, final UpdatePasswordRequest passwordRequest, HttpServletResponse response) {
        if (cookieService.isNotAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.UNAUTHORIZED.getCode());
        }

        final UserDto user = getUserFromCookies(request);

        return Optional.ofNullable(user)
                       .map(u -> changePassword(u, passwordRequest.password(), response))
                       .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.USER_CANNOT_BE_FOUND.getCode()));
    }

    private UserDto getUserFromCookies(final HttpServletRequest request) {
        final Long userId = cookieService.getUserId(request);

        return userRepository.findUserById(userId);
    }

    private ResponseEntity<String> changePassword(final UserDto userDto, final String password, HttpServletResponse response) {
        String hashedPassword = hashService.hash(password);

        if (Objects.equals(hashedPassword, userDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseStatus.PASSWORD_CANNOT_BE_SAME.getCode());
        }

        userDto.setPassword(hashedPassword);

        userRepository.updateUser(userDto);

        cookieService.updateCookies(userDto, response);

        return ResponseEntity.ok(ResponseStatus.PASSWORD_CHANGED_SUCCESSFULLY.getCode());
    }

    @Override
    public ResponseEntity<String> updateUsername(final HttpServletRequest request, final UpdateUsernameRequest usernameRequest, final HttpServletResponse response) {
        if (cookieService.isNotAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.UNAUTHORIZED.getCode());
        }

        final UserDto user = getUserFromCookies(request);

        if (userRepository.isUsernameNotUnique(usernameRequest.username())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseStatus.USERNAME_IS_TAKEN.getCode());
        }

        return Optional.ofNullable(user)
                       .map(u -> changeUsername(u, usernameRequest.username(), response))
                       .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.USER_CANNOT_BE_FOUND.getCode()));
    }

    private ResponseEntity<String> changeUsername(final UserDto userDto, final String username, final HttpServletResponse response) {

        userDto.setUsername(username);

        userRepository.updateUser(userDto);

        cookieService.updateCookies(userDto, response);

        return ResponseEntity.ok(ResponseStatus.USERNAME_CHANGED_SUCCESSFULLY.getCode());
    }

    @Override
    public ResponseEntity<String> deleteUser(final HttpServletRequest request, HttpServletResponse response) {
        if (cookieService.isNotAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.UNAUTHORIZED.getCode());
        }

        final UserDto user = getUserFromCookies(request);

        return Optional.ofNullable(user)
                       .map(userDto -> deleteUserById(request, userDto.getId(), response))
                       .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseStatus.USER_CANNOT_BE_FOUND.getCode()));
    }

    private ResponseEntity<String> deleteUserById(final HttpServletRequest request, final Long id, HttpServletResponse response) {
        userRepository.deleteUserById(id);

        cookieService.deleteAllCookies(request, response);

        return ResponseEntity.ok(ResponseStatus.USER_DELETED_SUCCESSFULLY.getCode());
    }
}
