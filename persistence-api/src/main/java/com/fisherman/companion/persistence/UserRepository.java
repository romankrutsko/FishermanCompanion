package com.fisherman.companion.persistence;

import java.util.List;

import com.fisherman.companion.dto.UserDto;

public interface UserRepository {
    void saveUser(UserDto user);
    UserDto findUserById(Long id);
    List<UserDto> findAllUsers();
    void updateUser(UserDto user);
    void deleteUserById(Long id);

    Long loginUser(String username, String password);
}
