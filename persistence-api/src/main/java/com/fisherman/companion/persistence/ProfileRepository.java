package com.fisherman.companion.persistence;

import java.util.Optional;

import com.fisherman.companion.dto.ProfileDto;

public interface ProfileRepository {
    void saveProfile(ProfileDto profile);
    Optional<ProfileDto> findProfileById(Long id);
    Optional<ProfileDto> findProfileByUserId(Long userId);
    void updateProfile(ProfileDto profile);
    void deleteProfileById(Long id);
}
