package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
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
                    INSERT INTO users (username, password, bio, location, contacts, role)
                    VALUES (:username, :password, :bio, :location, :contacts, :role)
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", user.username())
                .addValue("password", user.password())
                .addValue("bio", user.bio())
                .addValue("location", user.location())
                .addValue("contacts", user.contacts())
                .addValue("role", user.role());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void updateUserAvatar(final Long id, final String avatar) {
        final String sql = """
                UPDATE users
                SET avatar = :avatar
                WHERE id = :id
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("avatar", avatar);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean isUsernameNotUnique(String username) {
        final String sql = "SELECT COUNT(*) FROM users WHERE username = :username";

        final Integer count = namedParameterJdbcTemplate.queryForObject(sql, Map.of("username", username), Integer.class);

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
    public void updateUser(final UserDto user, final Long id) {
        final String sql = """
                    UPDATE users
                    SET password = COALESCE(:password, password),
                        bio = COALESCE(:bio, bio),
                        location = COALESCE(:location, location),
                        contacts = COALESCE(:contacts, contacts)
                    WHERE id = :id
                """;

        final MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("password", user.password())
                .addValue("bio", user.bio())
                .addValue("location", user.location())
                .addValue("contacts", user.contacts());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUserById(final Long id) {
        final String sql = """
                    DELETE FROM users WHERE id = :id
                """;

        namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }

    @Override
    public Long loginUser(final String username, final String password) {
        final String sql = """
                SELECT *
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
                          .avatar(rs.getString("avatar"))
                          .bio(rs.getString("bio"))
                          .location(rs.getString("location"))
                          .contacts(rs.getString("contacts"))
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
