package com.fisherman.companion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.PostDto;
import com.fisherman.companion.dto.RatingDto;
import com.fisherman.companion.dto.RequestDto;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.UserRole;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdateUserRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetUserToRateResponse;
import com.fisherman.companion.dto.response.UserResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.PostRepository;
import com.fisherman.companion.persistence.RatingRepository;
import com.fisherman.companion.persistence.RequestsRepository;
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
    private final RatingRepository ratingRepository;
    private final PostRepository postRepository;
    private final RequestsRepository requestsRepository;
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

        final Double averageRating = ratingRepository.getAverageRatingForUser(user.id());

        final Double rounded = Optional.ofNullable(averageRating).map(r -> BigDecimal.valueOf(r)
                                                                                .setScale(1, RoundingMode.HALF_UP)
                                                                                .doubleValue()).orElse(null);

        userResponse.setAverageRating(rounded);

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
    public GenericListResponse<UserResponse> findFutureTripMembers(final HttpServletRequest request, final Long postId) {
        final UserDto user = tokenService.verifyAuthentication(request);

        final List<UserResponse> response = getUserResponses(postId, user.id());

        return GenericListResponse.of(response);
    }

    private List<UserResponse> getUserResponses(final Long postId, final Long userId) {
        final PostDto post = postRepository.findPostById(postId);

        final List<Long> tripMembers = requestsRepository.getUserIdsOfAcceptedRequestsByPostId(postId, userId);

        if (!Objects.equals(post.getUserId(), userId)) {
            tripMembers.add(post.getUserId());
        }

        final List<UserDto> members = tripMembers.stream().map(userRepository::findUserById).toList();

        return members.stream().map(this::populateUserResponseWithAvgRating).toList();
    }

    @Override
    public GenericListResponse<GetUserToRateResponse> findFinishedTripMembersToRate(final HttpServletRequest request, final Long postId) {
        final UserDto user = tokenService.verifyAuthentication(request);

        final List<UserResponse> response = getUserResponses(postId, user.id());

        final List<GetUserToRateResponse> userToRateResponse = response.stream().map(userResponse -> {
            final boolean canBeRated = ratingRepository.canUserRate(postId, user.id(), userResponse.getId());

            return GetUserToRateResponse.builder()
                                        .user(userResponse)
                                        .canBeRated(canBeRated)
                                        .build();
        }).toList();

        return GenericListResponse.of(userToRateResponse);
    }

    @Override
    public Long loginUser(final String username, final String hashedPassword) {
        return userRepository.loginUser(username, hashedPassword);
    }

    @Override
    public String deleteUser(final HttpServletRequest request, HttpServletResponse response, Long userId) {
        tokenService.verifyAuthentication(request);

        deleteAllUserConnectedEntitiesByUserId(userId);

        userRepository.deleteUserById(userId);

        return ResponseStatus.USER_DELETED_SUCCESSFULLY.getCode();
    }

    private void deleteAllUserConnectedEntitiesByUserId(final Long userId) {
        final List<Long> userPosts = postRepository.findAllUserPostsIds(userId);
        final List<RequestDto> requestsToPosts = userPosts.stream()
                                                          .flatMap(postId -> requestsRepository.getRequestsByPostId(postId).stream())
                                                          .toList();

        requestsToPosts.forEach(requestDto -> requestsRepository.deleteRequest(requestDto.getId()));
        userPosts.forEach(postRepository::deleteById);

        final List<RequestDto> userRequests = requestsRepository.getRequestsByUserId(userId);
        userRequests.forEach(requestDto -> requestsRepository.deleteRequest(requestDto.getId()));

        final List<RatingDto> userRatings = ratingRepository.getRatingsWithCommentsForUser(userId);
        userRatings.forEach(ratingDto -> ratingRepository.deleteRatingById(ratingDto.id()));
    }
}
