package com.peopleeats.web.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigrations {
    public DatabaseMigrations(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN rider_name TEXT");
        } catch (Exception ignored) {
            // Column already exists or table not yet created.
        }
    }
}
