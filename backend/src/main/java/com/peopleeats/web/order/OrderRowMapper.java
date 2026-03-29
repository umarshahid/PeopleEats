package com.peopleeats.web.order;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<OrderRecord> {
    @Override
    public OrderRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderRecord(
                rs.getInt("order_no"),
                rs.getString("customer"),
                rs.getString("item"),
                rs.getDouble("price"),
                rs.getString("order_type"),
                rs.getString("state"),
                rs.getString("rider_name")
        );
    }
}
