package com.cartify.authservice.dao.mapper;

import com.cartify.authservice.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException{
        return User.builder()
                .id(resultSet.getLong("id"))
                .username(resultSet.getString("username"))
                .email(resultSet.getString("email"))
                .passwordHash(resultSet.getString("password_hash"))
                .mfaSecret(resultSet.getString("mfa_secret"))
                .status(resultSet.getString("status"))
                .build();
    }
}
