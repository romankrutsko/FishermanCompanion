package com.fisherman.companion.service;

import com.fisherman.companion.dto.request.ProfileRequest;
import com.fisherman.companion.dto.response.GetProfileResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface ProfileService {
    String createUserProfile(HttpServletRequest request, ProfileRequest profileRequest);

    String updateUserProfile(HttpServletRequest request, ProfileRequest profileRequest);

    GetProfileResponse getUserProfile(HttpServletRequest request);

    GetProfileResponse getUserProfileById(Long id);

    String deleteUserProfile(HttpServletRequest request);
}
