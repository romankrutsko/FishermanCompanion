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
    public static final String ID = "id";
    @Value("${token.expiration.time:86400}")
    private Integer maxAge;
    private final UserRepository userRepository;

    @Override
    public String generateToken(final String username, final String password) {
        return signToken(username, password);
    }

    private String signToken(final String username, final String password) {
        final Date currentTime = new Date();
        final Date expirationDate = getExpirationDate(currentTime, maxAge);
        final Key key = getKey(password);

        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(currentTime)
                   .setExpiration(expirationDate)
                   .signWith(key)
                   .compact();
    }

    private Key getKey(final String password) {
        final byte[] secretBytes = password.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private Date getExpirationDate(Date currentDate, Integer maxAge) {
        return new Date(currentDate.getTime() + maxAge);
    }

    @Override
    public boolean isTokenValid(final String token, final Long userId) {
        try {
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

    @Override
    public boolean isNotAuthenticated(HttpServletRequest request) {
        String token = getToken(request);

        Long userId = getUserId(request);

        return token == null || userId == null || !isTokenValid(token, userId);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return getTokenFromRequest(request);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return getCookieValue(request, TOKEN);
    }

    private Cookie getCookie(final HttpServletRequest request, String cookieName) {
        final List<Cookie> cookieList = Optional.ofNullable(request.getCookies())
                                                .map(Arrays::asList)
                                                .orElse(List.of());

        return cookieList.stream()
                         .filter(cookies -> cookies.getName().equals(cookieName))
                         .findFirst()
                         .orElse(null);
    }

    private String getCookieValue(final HttpServletRequest request, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);

        return Optional.ofNullable(cookie).map(Cookie::getValue).orElse(null);
    }

    @Override
    public Long getUserId(HttpServletRequest request) {
        String cookieUserId = getCookieValue(request, ID);

        return Optional.ofNullable(cookieUserId).map(Long::valueOf).orElse(null);
    }

    @Override
    public Cookie getCookieFromRequest(final HttpServletRequest request, String cookieName) {
        return getCookie(request, cookieName);
    }

    @Override
    public void updateCookies(final UserDto userDto, final HttpServletResponse response) {
        final String token = signToken(userDto.getUsername(), userDto.getPassword());

        final int maxAgeInSeconds = maxAge / 1000;

        final Cookie cookieToken = new Cookie(TOKEN, token);
        cookieToken.setPath("/");
        cookieToken.setHttpOnly(true);
        cookieToken.setMaxAge(maxAgeInSeconds);

        final Cookie cookieUserId = new Cookie(ID, userDto.getId().toString());
        cookieUserId.setPath("/");
        cookieUserId.setHttpOnly(true);
        cookieUserId.setMaxAge(maxAgeInSeconds);

        response.addCookie(cookieUserId);
        response.addCookie(cookieToken);
    }

    @Override
    public void deleteAllCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = getCookie(request, TOKEN);
        Cookie userId = getCookie(request, ID);

        if (token != null) {
            deleteCookie(token, response);
        }

        if (userId != null) {
            deleteCookie(userId, response);
        }
    }

    public void deleteCookie(final Cookie cookie, final HttpServletResponse response) {
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
