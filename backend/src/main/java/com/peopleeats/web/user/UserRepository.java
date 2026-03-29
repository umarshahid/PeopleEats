package com.peopleeats.web.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper = new UserRowMapper();

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserRecord> findByUsername(String username) {
        List<UserRecord> results = jdbcTemplate.query(
                "SELECT rowid as id, username, password, role FROM users WHERE username = ?",
                rowMapper,
                username
        );
        return results.stream().findFirst();
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM users WHERE username = ?",
                Integer.class,
                username
        );
        return count != null && count > 0;
    }

    public int insertUser(String username, String password, String role) {
        return jdbcTemplate.update(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                username,
                password,
                role
        );
    }

    public int updatePassword(Long id, String password) {
        return jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE rowid = ?",
                password,
                id
        );
    }
}
