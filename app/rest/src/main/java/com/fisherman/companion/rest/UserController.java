package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.request.CreateUserRequest;
import com.fisherman.companion.dto.request.UpdateUserRequest;
import com.fisherman.companion.dto.response.GenericListResponse;
import com.fisherman.companion.dto.response.GetUserToRateResponse;
import com.fisherman.companion.dto.response.UserResponse;
import com.fisherman.companion.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    Long createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable(value = "userId") Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("future/members/{postId}")
    GenericListResponse<UserResponse> getFutureTripMembers(HttpServletRequest request, @PathVariable(value = "postId") Long postId) {
        return userService.findFutureTripMembers(request, postId);
    }

    @GetMapping("finished/members/{postId}")
    GenericListResponse<GetUserToRateResponse> getFinishedTripMembersToRate(HttpServletRequest request, @PathVariable(value = "postId") Long postId) {
        return userService.findFinishedTripMembersToRate(request, postId);
    }

    @PatchMapping("/{userId}")
    UserResponse updateUser(HttpServletRequest request, @RequestBody UpdateUserRequest updateUserRequest, @PathVariable(value = "userId") Long userId) {
        return userService.updateUser(request, updateUserRequest, userId);
    }

    @PostMapping("/{userId}/avatar")
    String updateAvatar(HttpServletRequest request, @RequestPart("avatar") MultipartFile avatar, @PathVariable(value = "userId") Long userId) {
        return userService.updateUserAvatar(request, avatar, userId);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "userId") Long userId) {
        return userService.deleteUser(request, response, userId);
    }
}
