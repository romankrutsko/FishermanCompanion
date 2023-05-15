package com.fisherman.companion.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fisherman.companion.dto.ProfileDto;
import com.fisherman.companion.dto.request.ProfileRequest;
import com.fisherman.companion.service.ProfileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/create")
    ProfileDto createProfile(HttpServletRequest request, @RequestBody ProfileRequest profileRequest) {
        return profileService.createUserProfile(request, profileRequest);
    }

    @PostMapping("/save/avatar")
    String saveAvatar(HttpServletRequest request, @RequestPart("avatar") MultipartFile avatar) {
        return profileService.updateProfileAvatar(request, avatar);
    }

    @PostMapping("/update/{profileId}")
    String updateProfile(HttpServletRequest request, @RequestBody ProfileRequest profileRequest, @PathVariable(value = "profileId") Long profileId) {
        return profileService.updateUserProfile(request, profileRequest, profileId);
    }

    @GetMapping("/get/my")
    ProfileDto getProfile(HttpServletRequest request) {
        return profileService.getUserProfile(request);
    }

    @GetMapping("/get/{userId}")
    ProfileDto getProfile(@PathVariable(value = "userId") Long userId) {
        return profileService.getUserProfileByUserId(userId);
    }

    @DeleteMapping("/{profileId}")
    String deleteProfile(HttpServletRequest request, @PathVariable(value = "profileId") Long profileId) {
        return profileService.deleteUserProfile(request, profileId);
    }
}
