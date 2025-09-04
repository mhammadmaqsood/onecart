package com.cartify.authservice.dao;

import com.cartify.authservice.dao.mapper.UserRowMapper;
import com.cartify.authservice.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbc;

    @Override
    public Optional<User> findById(long id) {
        var sql = "SELECT * FROM users WHERE id = ?";
        return jdbc.query(sql, new UserRowMapper(), id).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        var sql = "SELECT * FROM users WHERE username = ?";
        return jdbc.query(sql, new UserRowMapper(), username).stream().findFirst();
    }

    @Override
    public long insert(User u) {
        var sql = """
            INSERT INTO users (username, email, password_hash, mfa_secret, status)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbc.update(sql, u.getUsername(), u.getEmail(), u.getPasswordHash(), u.getMfaSecret(), u.getStatus());
        // We can return generated key later; not needed for the health checks
        return 0L;
    }
}
