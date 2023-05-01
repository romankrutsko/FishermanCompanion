package com.fisherman.companion.service.utils;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.hash.Hashing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordHashService {
    @Value("${user.password.salt}")
    private static String salt;
    public static String hash(String password) {
        String saltedPassword = password + salt;
        return Hashing.sha256()
                      .hashString(saltedPassword, StandardCharsets.UTF_8)
                      .toString();
    }
}
