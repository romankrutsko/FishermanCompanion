package com.fisherman.companion.service;

import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.ProfileDto;
import com.fisherman.companion.dto.request.ProfileRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface ProfileService {
    ProfileDto createUserProfile(HttpServletRequest request, ProfileRequest profileRequest);

    String updateProfileAvatar(HttpServletRequest request, MultipartFile avatar);

    String updateUserProfile(HttpServletRequest request, ProfileRequest profileRequest);

    ProfileDto getUserProfile(HttpServletRequest request);

    ProfileDto getUserProfileByUserId(Long id);

    String deleteUserProfile(HttpServletRequest request, Long profileId);
}
