package com.fisherman.companion.dto.request;

public record ProfileRequest (
        String fullName,
        String bio,
        String location,
        String contacts
) {
}
