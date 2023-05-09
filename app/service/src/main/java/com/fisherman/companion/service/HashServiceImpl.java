package com.fisherman.companion.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;


@Service
public class HashServiceImpl implements HashService {
    @Value("${user.password.salt}")
    private String salt;
    @Override
    public String hash(final String key) {
        final String saltedPassword = key + salt;
        return Hashing.sha256()
                      .hashString(saltedPassword, StandardCharsets.UTF_8)
                      .toString();
    }
}
