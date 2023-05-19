package com.fisherman.companion.persistence;

import com.fisherman.companion.dto.User;

public interface UserRepository {
    Long saveUser(final User user);

    void updateUserAvatar(Long id, String avatar);

    boolean isUsernameNotUnique(String username);
    User findUserByUsername(String username);

    User findUserById(final Long id);

    void updateUser(User user, Long id);

    void deleteUserById(final Long id);

    Long loginUser(final String username, final String password);
}
