package com.fisherman.companion.service;

public interface TokenGenerateService {
    String generateToken(final String username, final String password, final Integer maxAgeInSeconds);

    boolean verifyToken(final String token);
}
