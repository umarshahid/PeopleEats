package com.peopleeats.web.feedback;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class FeedbackRepository {
    private final JdbcTemplate jdbcTemplate;

    public FeedbackRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(int orderNo, String riderName, int rating, String comment) {
        return jdbcTemplate.update(
                "INSERT INTO feedback (order_no, rider_name, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)",
                orderNo,
                riderName,
                rating,
                comment,
                Instant.now().toString()
        );
    }

    public Double getAverageRating(String riderName) {
        return jdbcTemplate.queryForObject(
                "SELECT AVG(rating) FROM feedback WHERE rider_name = ?",
                Double.class,
                riderName
        );
    }

    public Integer getTotalRatings(String riderName) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM feedback WHERE rider_name = ?",
                Integer.class,
                riderName
        );
    }

    public java.util.List<FeedbackEntry> getRecentFeedback(String riderName, int limit) {
        return jdbcTemplate.query(
                "SELECT order_no, rating, comment, created_at FROM feedback WHERE rider_name = ? ORDER BY id DESC LIMIT ?",
                (rs, rowNum) -> new FeedbackEntry(
                        rs.getInt("order_no"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getString("created_at")
                ),
                riderName,
                limit
        );
    }
}
