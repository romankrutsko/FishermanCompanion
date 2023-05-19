package com.fisherman.companion.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fisherman.companion.dto.SignTokenParams;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.dto.response.ResponseStatus;
import com.fisherman.companion.persistence.UserRepository;
import com.fisherman.companion.service.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    @Value("${token.expiration.time:86400}")
    private Integer maxAge;
    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    private Date getExpirationDate(Date currentDate, Integer maxAge) {
        return new Date(currentDate.getTime() + maxAge);
    }

    @Override
    public boolean isTokenValid(final String token) {
        try {
            final Key key = getKey(secret);

            final JwtParser jwtParser = Jwts.parserBuilder()
                                       .setSigningKey(key)
                                       .build();

            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getKey(final String secretKey) {
        final byte[] secretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    public UserDto verifyAuthentication(HttpServletRequest request) {
        if (isNotAuthenticated(request)) {
            throw new UnauthorizedException(ResponseStatus.UNAUTHORIZED.getCode());
        }
        return getUserFromToken(request);
    }

    private boolean isNotAuthenticated(final HttpServletRequest request) {
        final String token = getTokenFromRequest(request);

        return token == null || !isTokenValid(token);
    }

    private UserDto getUserFromToken(final HttpServletRequest request) {
        final String username = findUsernameFromToken(request);

        return userRepository.findUserByUsername(username);
    }

    private String findUsernameFromToken(HttpServletRequest request) {
        final String token = getTokenFromRequest(request);
        final Key key = getKey(secret);

        final JwtParser jwtParser = Jwts.parserBuilder()
                                        .setSigningKey(key)
                                        .build();


        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeaderValue = request.getHeader("Authorization");

        return Optional.ofNullable(authorizationHeaderValue).map(s -> s.substring(7)).orElse(null);
    }

    @Override
    public String generateToken(final SignTokenParams params, final HttpServletResponse response) {
        return signToken(params);
    }

    private String signToken(final SignTokenParams params) {
        final Date currentTime = new Date();
        final Date expirationDate = getExpirationDate(currentTime, maxAge);
        final Key key = getKey(secret);

        final Claims claims = Jwts.claims().setSubject(params.username());
        claims.put("id", params.id());
        claims.put("role", params.role());

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(currentTime)
                   .setExpiration(expirationDate)
                   .signWith(key)
                   .compact();
    }
}
