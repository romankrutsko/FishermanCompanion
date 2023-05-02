package com.fisherman.companion.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.UserRole;
import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.persistence.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final HashService hashService;

    @Override
    public ResponseEntity<String> createUser(final CreateUserRequest createUserRequest) {
        final String hashedPassword = hashService.hash(createUserRequest.password());

        final UserDto userDto = new UserDto();
        userDto.setEmail(createUserRequest.email());
        userDto.setPassword(hashedPassword);
        userDto.setUsername(createUserRequest.username());
        userDto.setRole(UserRole.USER.name().toLowerCase());

        userRepository.saveUser(userDto);
        return ResponseEntity.ok("User created successfully");
    }
}
