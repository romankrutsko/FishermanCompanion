package com.fisherman.companion.persistence;

import com.fisherman.companion.dto.ProfileDto;

public interface ProfileRepository {
    Long saveProfile(final ProfileDto profile);

    void updateProfileAvatar(Long userId, String avatar);

    ProfileDto findProfileById(final Long id);
    ProfileDto findProfileByUserId(final Long userId);
    void updateProfile(final ProfileDto profile);
    void deleteProfileByUserId(final Long userId);
}
