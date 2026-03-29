package com.peopleeats.web.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final OrderRowMapper rowMapper = new OrderRowMapper();

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrderRecord> findAll() {
        return jdbcTemplate.query(
                "SELECT order_no, customer, item, price, order_type, state, rider_name FROM orders ORDER BY order_no DESC",
                rowMapper
        );
    }

    public List<OrderRecord> findByState(String state) {
        return jdbcTemplate.query(
                "SELECT order_no, customer, item, price, order_type, state, rider_name FROM orders WHERE state = ? ORDER BY order_no DESC",
                rowMapper,
                state
        );
    }

    public Optional<OrderRecord> findByOrderNo(int orderNo) {
        List<OrderRecord> results = jdbcTemplate.query(
                "SELECT order_no, customer, item, price, order_type, state, rider_name FROM orders WHERE order_no = ?",
                rowMapper,
                orderNo
        );
        return results.stream().findFirst();
    }

    public int insert(String customer, String item, double price, String orderType, String state) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO orders (customer, item, price, order_type, state) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, customer);
            ps.setString(2, item);
            ps.setDouble(3, price);
            ps.setString(4, orderType);
            ps.setString(5, state);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? -1 : key.intValue();
    }

    public int updateState(int orderNo, String state) {
        return jdbcTemplate.update(
                "UPDATE orders SET state = ? WHERE order_no = ?",
                state,
                orderNo
        );
    }

    public int updateStateWithRider(int orderNo, String state, String riderName) {
        return jdbcTemplate.update(
                "UPDATE orders SET state = ?, rider_name = ? WHERE order_no = ?",
                state,
                riderName,
                orderNo
        );
    }
}
