package com.fisherman.companion.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fisherman.companion.dto.UserDto;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveUser(UserDto user) {
        String sql = """
                    INSERT INTO users (username, email, password, role)
                    VALUES (:username, :email, :password, :role)
                """;
        Map<String, Object> params = Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "password", user.getPassword(),
                "role", user.getRole()
        );
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public UserDto findUserById(Long id) {
        String sql = """
                    SELECT *
                    FROM users
                    WHERE id = :id
                """;
        Map<String, Object> params = Map.of("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new UserMapper());
    }

    @Override
    public List<UserDto> findAllUsers() {
        String sql = """
                    SELECT *
                    FROM users
                """;
        return namedParameterJdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public void updateUser(UserDto user) {
        String sql = """
                    UPDATE users
                    SET username = :username,
                        email = :email,
                        password = :password,
                        role = :role
                    WHERE id = :id
                """;
        Map<String, Object> params = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "password", user.getPassword(),
                "role", user.getRole()
        );
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUserById(Long id) {
        String sql = """
                    DELETE u, p FROM user u LEFT JOIN profiles p ON u.id = p.user_id
                    WHERE u.id = :id
                """;
        Map<String, Object> params = Map.of("id", id);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Long loginUser(String username, String password) {
        final String sql = """
                SELECT id
                FROM users
                WHERE username = :username AND password = :password
                """;
        Map<String, Object> params = Map.of(
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
