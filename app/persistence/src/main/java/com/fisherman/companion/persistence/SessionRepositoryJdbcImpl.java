package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.SessionDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryJdbcImpl implements SessionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveSession(final SessionDto sessionDto) {
        final String sql = """
                INSERT INTO sessions (user_id, token, creation_time, expiration_time)
                VALUES (:user_id, :token, :creation_time, :expiration_time)
                """;

        final Map<String, Object> params = Map.of(
                "user_id", sessionDto.getUserId(),
                "token", sessionDto.getToken(),
                "creation_time", sessionDto.getCreationTime(),
                "expiration_time", sessionDto.getExpirationTime()
        );

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Long getUserIdByToken(final String token) {
        final String sql = """
                SELECT user_id
                FROM sessions
                WHERE token = :token
                AND expiration_time > NOW()
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("token", token), new UserIdMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    @Override
    public void deleteExpiredSessions(final LocalDateTime currentTime) {
        final String sql = """
                DELETE FROM sessions
                WHERE expiration_time < :currentTime
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("currentTime", currentTime));
    }

    @Override
    public void deleteSessionByToken(final String token) {
        final String sql = """
                DELETE FROM sessions
                WHERE sessions.token = :token
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("token", token));
    }

    private static class UserIdMapper implements RowMapper<Long> {
        @Override
        public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return rs.getLong("user_id");
        }
    }
}
