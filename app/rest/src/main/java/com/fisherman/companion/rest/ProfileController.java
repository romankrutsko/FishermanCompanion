package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisherman.companion.dto.request.ProfileRequest;
import com.fisherman.companion.dto.response.GetProfileResponse;
import com.fisherman.companion.service.ProfileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/create")
    String createProfile(HttpServletRequest request, @RequestBody ProfileRequest profileRequest) {
        return profileService.createUserProfile(request, profileRequest);
    }

    @PostMapping("/update")
    String updateProfile(HttpServletRequest request, @RequestBody ProfileRequest profileRequest) {
        return profileService.updateUserProfile(request, profileRequest);
    }

    @PostMapping("/delete")
    String deleteProfile(HttpServletRequest request) {
        return profileService.deleteUserProfile(request);
    }

    @GetMapping("/get/my")
    GetProfileResponse getProfile(HttpServletRequest request) {
        return profileService.getUserProfile(request);
    }

    @GetMapping("/get/{userId}")
    GetProfileResponse getProfile(@PathVariable(value = "userId") Long userId) {
        return profileService.getUserProfileById(userId);
    }
}
