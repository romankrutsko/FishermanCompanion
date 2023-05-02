package com.fisherman.companion.service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.persistence.SessionRepository;
import com.fisherman.companion.persistence.UserRepository;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenGenerateServiceImpl implements TokenGenerateService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    @Override
    public String generateToken(final String username, final String password, final Integer maxAgeInSeconds) {
        final Date currentTime = new Date();
        final Date expirationDate = getExpirationDate(currentTime, maxAgeInSeconds);
        final Key key = getKey(password);

        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(currentTime)
                   .setExpiration(expirationDate)
                   .signWith(key)
                   .compact();
    }

    @Override
    public boolean verifyToken(final String token) {
        try {
            final Long userId = sessionRepository.getUserIdByToken(token);
            final UserDto user = userRepository.findUserById(userId);
            final Key key = getKey(user.getPassword());

            final JwtParser jwtParser = Jwts.parserBuilder()
                                       .setSigningKey(key)
                                       .build();

            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getKey(final String password) {
        final byte[] secretBytes = password.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private Date getExpirationDate(Date currentDate, Integer maxAge) {
        final int expirationTimeInMs = maxAge * 1000;
        return new Date(currentDate.getTime() + expirationTimeInMs);
    }
}
