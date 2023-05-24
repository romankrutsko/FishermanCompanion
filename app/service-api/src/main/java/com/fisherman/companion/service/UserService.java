package com.fisherman.companion.service;

import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdateUserRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetUserToRateResponse;
import com.fisherman.companion.dto.response.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    Long createUser(CreateUserRequest createUserRequest);

    String updateUserAvatar(HttpServletRequest request, MultipartFile avatar, Long id);

    UserResponse findUserById(Long userId);

    UserResponse updateUser(HttpServletRequest request, UpdateUserRequest updateUserRequest, Long id);

    GenericListResponse<UserResponse> findFutureTripMembers(HttpServletRequest request, Long postId);

    GenericListResponse<GetUserToRateResponse> findFinishedTripMembersToRate(HttpServletRequest request, Long postId);

    Long loginUser(String username, String hashedPassword);

    String deleteUser(HttpServletRequest request, HttpServletResponse response, Long userId);
}
