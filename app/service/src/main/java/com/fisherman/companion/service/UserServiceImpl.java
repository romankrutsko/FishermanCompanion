package com.fisherman.companion.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.UserRole;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdateUserRequest;
import com.fisherman.companion.dto.response.UserResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.UserRepository;
import com.fisherman.companion.service.exception.RequestException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RatingService ratingService;
    private final CloudStorageService cloudStorageService;
    private final HashService hashService;

    @Override
    public Long createUser(final CreateUserRequest createUserRequest) {
        final String username = createUserRequest.username();
        final String hashedPassword = hashService.hash(createUserRequest.password());

        if (userRepository.isUsernameNotUnique(username)) {
            throw new RequestException(ResponseStatus.USERNAME_IS_TAKEN.getCode());
        }

        return saveUser(createUserRequest, hashedPassword);
    }

    private Long saveUser(final CreateUserRequest createUserRequest, final String hashedPassword) {
        final UserDto user = UserDto.builder()
                                    .username(createUserRequest.username())
                                    .password(hashedPassword)
                                    .bio(createUserRequest.bio())
                                    .location(createUserRequest.location())
                                    .contacts(createUserRequest.contacts())
                                    .role(UserRole.USER.name())
                                    .build();

        return userRepository.saveUser(user);
    }

    @Override
    public String updateUserAvatar(final HttpServletRequest request, final MultipartFile avatar, final Long id) {
        tokenService.verifyAuthentication(request);

        final String avatarUrl = saveAvatar(avatar);

        userRepository.updateUserAvatar(id, avatarUrl);

        return avatarUrl;
    }

    private String saveAvatar(final MultipartFile avatar) {
        return Optional.ofNullable(avatar)
                       .map(file -> {
                           try {
                               return cloudStorageService.uploadFile(file);
                           } catch (Exception e) {
                               throw new RequestException(ResponseStatus.UNABLE_TO_UPLOAD_FILE.getCode());
                           }
                       })
                       .orElse(null);
    }

    @Override
    public UserResponse findUserById(final Long userId) {
        final UserDto user = userRepository.findUserById(userId);

        return populateUserResponseWithAvgRating(user);
    }

    private UserResponse populateUserResponseWithAvgRating(final UserDto user) {
        final UserResponse userResponse = mapUserToResponse(user);

        final Double averageRating = ratingService.getUserAverageRatingByUserId(user.id());

        userResponse.setAverageRating(averageRating);

        return userResponse;
    }

    private UserResponse mapUserToResponse(final UserDto user) {
        return UserResponse.builder()
                           .id(user.id())
                           .username(user.username())
                           .avatar(user.avatar())
                           .bio(user.bio())
                           .location(user.location())
                           .contacts(user.contacts())
                           .role(user.role())
                           .build();
    }

    @Override
    public UserResponse updateUser(final HttpServletRequest request, final UpdateUserRequest updateUserRequest, final Long id) {
        final UserDto user = tokenService.verifyAuthentication(request);
        String hashedPassword = null;

        if (updateUserRequest.password() != null) {
            hashedPassword = hashService.hash(updateUserRequest.password());
        }

        final UserDto userToUpdate = UserDto.builder()
                                            .password(hashedPassword)
                                            .bio(updateUserRequest.bio())
                                            .location(updateUserRequest.location())
                                            .contacts(updateUserRequest.contacts())
                                            .build();

        userRepository.updateUser(userToUpdate, id);

        return populateUserResponse(user.username());
    }

    private UserResponse populateUserResponse(final String username) {
        final UserDto updatedUser = userRepository.findUserByUsername(username);

        return populateUserResponseWithAvgRating(updatedUser);
    }

    @Override
    public Long loginUser(final String username, final String hashedPassword) {
        return userRepository.loginUser(username, hashedPassword);
    }

    @Override
    public String deleteUser(final HttpServletRequest request, HttpServletResponse response, Long userId) {
        tokenService.verifyAuthentication(request);

        userRepository.deleteUserById(userId);

        return ResponseStatus.USER_DELETED_SUCCESSFULLY.getCode();
    }
}
