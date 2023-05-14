package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Long saveUser(final UserDto user) {
        final String sql = """
                    INSERT INTO users (username, email, password, role)
                    VALUES (:username, :email, :password, :role)
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
    @Override
    public boolean isUsernameNotUnique(String username) {
        final String sql = "SELECT COUNT(*) FROM users WHERE username = :username";

        final Integer count = namedParameterJdbcTemplate.queryForObject(sql, Map.of("username", username), Integer.class);

        return (count != null && count > 0);
    }

    @Override
    public boolean isEmailNotUnique(String email) {
        final String sql = "SELECT COUNT(*) FROM users WHERE email = :email";

        final Integer count = namedParameterJdbcTemplate.queryForObject(sql, Map.of("email", email), Integer.class);

        return (count != null && count > 0);
    }

    @Override
    public UserDto findUserByUsername(final String username) {
        final String sql = """
                    SELECT *
                    FROM users
                    WHERE username = :username
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("username", username), new UserMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    @Override
    public UserDto findUserById(final Long id) {
        final String sql = """
                    SELECT *
                    FROM users
                    WHERE id = :id
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("id", id), new UserMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    @Override
    public List<UserDto> findAllUsers() {
        final String sql = """
                    SELECT *
                    FROM users
                """;
        return namedParameterJdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public void updateUser(final UserDto user) {
        final String sql = """
                    UPDATE users
                    SET username = COALESCE(:usermame, username),
                        email = COALESCE(:email, email),
                        password = COALESCE(:password, password),
                        role = COALESCE(:role, role)
                    WHERE id = :id
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUserById(final Long id) {
        final String sql = """
                    DELETE u, p FROM users u LEFT JOIN profiles p ON u.id = p.user_id
                    WHERE u.id = :id
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }

    @Override
    public Long loginUser(final String username, final String password) {
        final String sql = """
                SELECT id
                FROM users
                WHERE username = :username AND password = :password
                """;
        final Map<String, Object> params = Map.of(
                "username", username,
                "password", password
        );
        return namedParameterJdbcTemplate.query(sql, params, new UserIdMapper())
                                         .stream()
                                         .findFirst()
                                         .orElse(null);
    }

    private static class UserMapper implements RowMapper<UserDto> {
        @Override
        public UserDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return UserDto.builder()
                          .id(rs.getLong("id"))
                          .username(rs.getString("username"))
                          .email(rs.getString("email"))
                          .password(rs.getString("password"))
                          .role(rs.getString("role"))
                          .build();
        }
    }

    private static class UserIdMapper implements RowMapper<Long> {
        @Override
        public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return rs.getLong("id");
        }
    }

}
