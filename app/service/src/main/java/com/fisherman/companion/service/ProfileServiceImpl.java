package com.fisherman.companion.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.ProfileDto;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.request.ProfileRequest;
import com.fisherman.companion.dto.response.GetProfileResponse;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.ProfileRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CookieService cookieService;

    @Override
    public String createUserProfile(final HttpServletRequest request, final ProfileRequest profileRequest) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final ProfileDto profile = mapToProfileDto(profileRequest, user);

        profileRepository.saveProfile(profile);

        return ResponseStatus.PROFILE_CREATED_SUCCESSFULLY.getCode();
    }

    private ProfileDto mapToProfileDto(final ProfileRequest request, final UserDto user) {
        return ProfileDto.builder()
                         .userId(user.getId())
                         .fullName(request.fullName())
                         .avatar(request.avatar())
                         .bio(request.bio())
                         .location(request.location())
                         .contacts(request.contacts())
                         .build();
    }

    @Override
    public String updateUserProfile(final HttpServletRequest request, final ProfileRequest profileRequest) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final ProfileDto profileDto = mapToProfileDto(profileRequest, user);

        profileRepository.updateProfile(profileDto);

        return ResponseStatus.USER_PROFILE_UPDATED_SUCCESSFULLY.getCode();
    }

    @Override
    public GetProfileResponse getUserProfile(final HttpServletRequest request) {
        final UserDto user = cookieService.verifyAuthentication(request);

        final ProfileDto profile = profileRepository.findProfileByUserId(user.getId());

        return Optional.ofNullable(profile).map(this::mapToProfileResponse).orElse(null);
    }

    @Override
    public GetProfileResponse getUserProfileById(final Long id) {
        final ProfileDto profile = profileRepository.findProfileByUserId(id);

        return Optional.ofNullable(profile).map(this::mapToProfileResponse).orElse(null);
    }

    private GetProfileResponse mapToProfileResponse(final ProfileDto profileDto) {
        return GetProfileResponse.builder()
                                 .fullName(profileDto.getFullName())
                                 .avatar(profileDto.getAvatar())
                                 .bio(profileDto.getBio())
                                 .location(profileDto.getLocation())
                                 .contacts(profileDto.getContacts())
                                 .build();
    }

    @Override
    public String deleteUserProfile(final HttpServletRequest request) {
        final UserDto user = cookieService.verifyAuthentication(request);

        profileRepository.deleteProfileByUserId(user.getId());

        return ResponseStatus.PROFILE_DELETED_SUCCESSFULLY.getCode();
    }
}
