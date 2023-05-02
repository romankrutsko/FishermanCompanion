package com.fisherman.companion.persistence;

import java.util.List;

import com.fisherman.companion.dto.UserDto;

public interface UserRepository {
    void saveUser(final UserDto user);
    UserDto findUserById(final Long id);
    List<UserDto> findAllUsers();
    void updateUser(final UserDto user);
    void deleteUserById(final Long id);

    Long loginUser(final String username, final String password);
}
