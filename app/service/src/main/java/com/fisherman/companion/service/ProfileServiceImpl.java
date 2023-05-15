package com.fisherman.companion.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.ProfileDto;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.ProfileRequest;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.ProfileRepository;
import com.fisherman.companion.service.exception.RequestException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CookieService cookieService;
    private final CloudStorageService cloudStorageService;

    @Override
    public ProfileDto createUserProfile(final HttpServletRequest request, final ProfileRequest profileRequest) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final ProfileDto profile = mapToProfileDto(profileRequest, user.getId(), null);

        Long profileId = profileRepository.saveProfile(profile);

        profile.setId(profileId);

        return profile;
    }

    private ProfileDto mapToProfileDto(final ProfileRequest request, final Long userId, final Long profileId) {
        return ProfileDto.builder()
                         .id(profileId)
                         .userId(userId)
                         .fullName(request.fullName())
                         .bio(request.bio())
                         .location(request.location())
                         .contacts(request.contacts())
                         .build();
    }

    @Override
    public String updateProfileAvatar(final HttpServletRequest request, final MultipartFile avatar) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final String avatarUrl = saveAvatar(avatar);

        profileRepository.updateProfileAvatar(user.getId(), avatarUrl);

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
    public String updateUserProfile(final HttpServletRequest request, final ProfileRequest profileRequest, final Long profileId) {
        cookieService.verifyAuthentication(request);

        final ProfileDto profileDto = mapToProfileDto(profileRequest, null, profileId);

        profileRepository.updateProfile(profileDto);

        return ResponseStatus.USER_PROFILE_UPDATED_SUCCESSFULLY.getCode();
    }

    @Override
    public ProfileDto getUserProfile(final HttpServletRequest request) {
        final UserDto user = cookieService.verifyAuthentication(request);

        return profileRepository.findProfileByUserId(user.getId());
    }

    @Override
    public ProfileDto getUserProfileByUserId(final Long id) {
        return profileRepository.findProfileByUserId(id);
    }

    @Override
    public String deleteUserProfile(final HttpServletRequest request, final Long profileId) {
        cookieService.verifyAuthentication(request);

        profileRepository.deleteProfileByUserId(profileId);

        return ResponseStatus.PROFILE_DELETED_SUCCESSFULLY.getCode();
    }
}
