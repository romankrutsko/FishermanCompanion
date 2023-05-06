package com.fisherman.companion.service;

import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.UserRole;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdatePasswordRequest;
import com.fisherman.companion.dto.request.UpdateUsernameRequest;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.UserRepository;
import com.fisherman.companion.service.exception.RequestException;
import com.fisherman.companion.service.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.EmailValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private final HashService hashService;

    @Override
    public String createUser(final CreateUserRequest createUserRequest) {
        final String username = createUserRequest.username();
        final String email = createUserRequest.email();
        final String hashedPassword = hashService.hash(createUserRequest.password());

        if (isNotValidEmail(email)) {
            throw new RequestException(ResponseStatus.EMAIL_IS_NOT_VALID.getCode());
        }

        if (userRepository.isUsernameNotUnique(username)) {
            throw new RequestException(ResponseStatus.USERNAME_IS_TAKEN.getCode());
        }

        if (userRepository.isEmailNotUnique(email)) {
            throw new RequestException(ResponseStatus.EMAIL_IS_TAKEN.getCode());
        }

        return saveUser(createUserRequest, hashedPassword);
    }

    public boolean isNotValidEmail(String email) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        return !emailValidator.isValid(email);
    }

    private String saveUser(final CreateUserRequest createUserRequest, final String hashedPassword) {
        final UserDto userDto = new UserDto();
        userDto.setEmail(createUserRequest.email());
        userDto.setPassword(hashedPassword);
        userDto.setUsername(createUserRequest.username());
        userDto.setRole(UserRole.USER.name().toLowerCase());

        userRepository.saveUser(userDto);
        return ResponseStatus.USER_CREATED_SUCCESSFULLY.getCode();
    }

    @Override
    public String updateUserPassword(final HttpServletRequest request, final UpdatePasswordRequest passwordRequest) {
        final UserDto user = cookieService.verifyAuthentication(request);

        return Optional.ofNullable(user)
                       .map(u -> changePassword(u, passwordRequest.password()))
                       .orElseThrow(() -> new UnauthorizedException(ResponseStatus.USER_CANNOT_BE_FOUND.getCode()));
    }

    private String changePassword(final UserDto userDto, final String password) {
        String hashedPassword = hashService.hash(password);

        if (Objects.equals(hashedPassword, userDto.getPassword())) {
            throw new RequestException(ResponseStatus.PASSWORD_CANNOT_BE_SAME.getCode());
        }

        userDto.setPassword(hashedPassword);

        userRepository.updateUser(userDto);

        return ResponseStatus.PASSWORD_CHANGED_SUCCESSFULLY.getCode();
    }

    @Override
    public String updateUsername(final HttpServletRequest request, final UpdateUsernameRequest usernameRequest, final HttpServletResponse response) {
        final UserDto user = cookieService.verifyAuthentication(request);

        if (userRepository.isUsernameNotUnique(usernameRequest.username())) {
            throw new RequestException(ResponseStatus.USERNAME_IS_TAKEN.getCode());
        }

        return Optional.ofNullable(user)
                       .map(u -> changeUsername(u, usernameRequest.username(), response))
                       .orElseThrow(() -> new UnauthorizedException(ResponseStatus.USER_CANNOT_BE_FOUND.getCode()));
    }

    private String changeUsername(final UserDto userDto, final String username, final HttpServletResponse response) {

        userDto.setUsername(username);

        userRepository.updateUser(userDto);

        cookieService.updateCookies(userDto, response);

        return ResponseStatus.USERNAME_CHANGED_SUCCESSFULLY.getCode();
    }

    @Override
    public String deleteUser(final HttpServletRequest request, HttpServletResponse response) {
        final UserDto user = cookieService.verifyAuthentication(request);

        return Optional.ofNullable(user)
                       .map(userDto -> deleteUserById(request, userDto.getId(), response))
                       .orElseThrow(() -> new UnauthorizedException(ResponseStatus.USER_CANNOT_BE_FOUND.getCode()));
    }

    private String deleteUserById(final HttpServletRequest request, final Long id, HttpServletResponse response) {
        userRepository.deleteUserById(id);

        cookieService.deleteAllCookies(request, response);

        return ResponseStatus.USER_DELETED_SUCCESSFULLY.getCode();
    }
}
