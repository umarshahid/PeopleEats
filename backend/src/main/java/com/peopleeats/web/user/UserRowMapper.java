package com.peopleeats.web.user;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<UserRecord> {
    @Override
    public UserRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserRecord(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}
