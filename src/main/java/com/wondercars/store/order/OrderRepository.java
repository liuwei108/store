package com.wondercars.store.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {
    private JdbcTemplate jdbc;
    private NamedParameterJdbcTemplate npjdbc;

    @Autowired
    public OrderRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate npjdbc) {
        this.jdbc = jdbc;
        this.npjdbc = npjdbc;
    }

    public Map findOrder(int id) {
        return jdbc.queryForMap("SELECT ORDERS.*, PRODUCT.name FROM ORDERS INNER JOIN PRODUCT ON (ORDERS.productId = PRODUCT.id) WHERE ORDERS.id = ?", id);
    }

    public int createOrder(Map order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String INSERT_SQL = "INSERT INTO ORDERS (userId, productId, amount) VALUES (?, ?, ?)";
        jdbc.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(INSERT_SQL,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, (int) order.get("userId"));
            ps.setInt(2, (int) order.get("productId"));
            ps.setBigDecimal(3, (BigDecimal) order.get("amount"));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public int updateOrder(Map order) {
        String status = (String) order.get("status");
        int id = (int) order.get("id");
        return jdbc.update("UPDATE ORDERS SET status = ? WHERE id = ?", status, id);
    }

    public List getOrders() {
        return jdbc.queryForList("SELECT ORDERS.*, PRODUCT.name FROM ORDERS INNER JOIN PRODUCT ON (ORDERS.productId = PRODUCT.id) ORDER id DESC");
    }

    public Map getProduct(int id) {
        return jdbc.queryForMap("SELECT * FROM PRODUCT WHERE id = ?", id);
    }

    public List getProducts() {
        return jdbc.queryForList("SELECT * FROM PRODUCT");
    }
}
