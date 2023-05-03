package com.fisherman.companion.persistence;

import com.fisherman.companion.dto.ProfileDto;

public interface ProfileRepository {
    void saveProfile(final ProfileDto profile);
    ProfileDto findProfileById(final Long id);
    ProfileDto findProfileByUserId(final Long userId);
    void updateProfile(final ProfileDto profile);
    void deleteProfileById(final Long id);
}
