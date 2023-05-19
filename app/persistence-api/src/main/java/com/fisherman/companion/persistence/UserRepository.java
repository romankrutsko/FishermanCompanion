package com.fisherman.companion.persistence;

import com.fisherman.companion.dto.UserDto;

public interface UserRepository {
    Long saveUser(final UserDto user);

    void updateUserAvatar(Long id, String avatar);

    boolean isUsernameNotUnique(String username);
    UserDto findUserByUsername(String username);

    UserDto findUserById(final Long id);

    void updateUser(UserDto user, Long id);

    void deleteUserById(final Long id);

    Long loginUser(final String username, final String password);
}
