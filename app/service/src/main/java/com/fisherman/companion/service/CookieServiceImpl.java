package com.fisherman.companion.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fisherman.companion.dto.UserDto;
import com.fisherman.companion.persistence.UserRepository;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {
    public static final String TOKEN = "token";
    @Value("${token.expiration.time:86400}")
    private Integer maxAge;
    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    private String signToken(final String username) {
        final Date currentTime = new Date();
        final Date expirationDate = getExpirationDate(currentTime, maxAge);
        final Key key = getKey(secret);

        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(currentTime)
                   .setExpiration(expirationDate)
                   .signWith(key)
                   .compact();
    }

    private Key getKey(final String secretKey) {
        final byte[] secretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

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

    private String findUsernameFromToken(HttpServletRequest request) {
        final String token = getTokenFromRequest(request);
        final Key key = getKey(secret);

        final JwtParser jwtParser = Jwts.parserBuilder()
                                        .setSigningKey(key)
                                        .build();


        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public boolean isNotAuthenticated(HttpServletRequest request) {
        String token = getToken(request);

        return token == null || !isTokenValid(token);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return getTokenFromRequest(request);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return getCookieValue(request);
    }

    private Cookie getCookie(final HttpServletRequest request) {
        final List<Cookie> cookieList = Optional.ofNullable(request.getCookies())
                                                .map(Arrays::asList)
                                                .orElse(List.of());

        return cookieList.stream()
                         .filter(cookies -> cookies.getName().equals(CookieServiceImpl.TOKEN))
                         .findFirst()
                         .orElse(null);
    }

    private String getCookieValue(final HttpServletRequest request) {
        Cookie cookie = getCookie(request);

        return Optional.ofNullable(cookie).map(Cookie::getValue).orElse(null);
    }

    @Override
    public UserDto getUserFromCookies(final HttpServletRequest request) {
        final String username = findUsernameFromToken(request);

        return userRepository.findUserByUsername(username);
    }

    @Override
    public void updateCookies(final UserDto userDto, final HttpServletResponse response) {
        final String token = signToken(userDto.getUsername());

        final int maxAgeInSeconds = maxAge / 1000;

        final Cookie cookieToken = new Cookie(TOKEN, token);
        cookieToken.setPath("/");
        cookieToken.setHttpOnly(true);
        cookieToken.setMaxAge(maxAgeInSeconds);

        response.addCookie(cookieToken);
    }

    @Override
    public void deleteAllCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = getCookie(request);

        if (token != null) {
            deleteCookie(token, response);
        }
    }

    public void deleteCookie(final Cookie cookie, final HttpServletResponse response) {
        cookie.setValue("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
