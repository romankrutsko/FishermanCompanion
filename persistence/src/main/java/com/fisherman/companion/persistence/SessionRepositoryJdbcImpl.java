package com.fisherman.companion.persistence;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.SessionDto;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class SessionRepositoryJdbcImpl implements SessionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveSession(SessionDto sessionDto) {
        String sql = """
                INSERT INTO sessions (user_id, token, creation_time, expiration_time)
                VALUES (:user_id, :token, :creation_time, :expiration_time)
                """;
        Map<String, Object> params = Map.of(
                "user_id", sessionDto.getUserId(),
                "token", sessionDto.getToken(),
                "creation_time", sessionDto.getCreationTime(),
                "expiration_time", sessionDto.getExpirationTime()
        );

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Integer getUserIdByToken(String token) {
        String sql = """
                SELECT user_id
                FROM sessions
                WHERE token = :token
                AND expiration_time > NOW()
                """;
        Map<String, Object> params = Map.of("token", token);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    @Override
    public void deleteExpiredSessions(LocalDateTime currentTime) {
        String sql = """
                DELETE FROM sessions
                WHERE expiration_time < :currentTime
                """;
        Map<String, Object> params = Map.of("currentTime", currentTime);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
