package com.fisherman.companion.dto.request;

public record ProfileRequest (
        String fullName,
        String avatar,
        String bio,
        String location,
        String contacts
) {
}
