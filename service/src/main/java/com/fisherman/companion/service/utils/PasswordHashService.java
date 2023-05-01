package com.fisherman.companion.service.utils;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordHashService {
    public static String hash(String input) {
        return Hashing.sha256()
                      .hashString(input, StandardCharsets.UTF_8)
                      .toString();
    }
}
